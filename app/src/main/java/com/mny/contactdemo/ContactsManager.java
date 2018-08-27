package com.mny.contactdemo;

import android.accounts.AccountManager;
import android.content.ContentProviderOperation;
import android.content.ContentProviderResult;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.util.logging.Logger;

import android.provider.ContactsContract.CommonDataKinds.Phone;
import android.widget.Toast;

import static android.provider.ContactsContract.Directory.ACCOUNT_NAME;
import static android.provider.SyncStateContract.Columns.ACCOUNT_TYPE;


/**
 * Created by MnyZhao on 2017/12/5.
 */

public class ContactsManager {
    private Context mContext;
    private String PHONEBOOK_LABEL;
    private static final int CONTACT_ID = 0;
    private static final int DISPLAY_NAME = 1;
    private static final int NUMBER = 2;
    private static final int PHOTO_URI = 3;
    private static final int TYPE = 4;
    private static final int PHONEBOOK_LABELINT = 5;

    private static String[] PHONES_PROJECTION;

    public ContactsManager(Context mContext) {
        this.mContext = mContext;
        //如果android操作系统版本4.4或4.4以上就要用phonebook_label而不是sort_key字段
        if (android.os.Build.VERSION.SDK_INT >= 19) {
            PHONEBOOK_LABEL = "phonebook_label";
        } else {
            PHONEBOOK_LABEL = ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY;
        }
        PHONES_PROJECTION = new String[]{
                Phone.CONTACT_ID,
                Phone.DISPLAY_NAME,
                Phone.NUMBER,
                Phone.PHOTO_URI,
                Phone.TYPE,
                PHONEBOOK_LABEL};
    }

    /**
     * 获取联系人信息  单个cursor 用LinekedMap 去重
     * 并用";"隔开类型和号码
     *
     * @return
     */
    public List<ContactsEntity> getAllContacts() {
        List<ContactsEntity> lists = new ArrayList<ContactsEntity>();
        LinkedHashMap<String, ContactsEntity> LinkedHashMap = new LinkedHashMap<>();
        ContentResolver resolver = mContext.getContentResolver();
        ContactsEntity bean = new ContactsEntity();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PHONES_PROJECTION, null, null, Phone.SORT_KEY_PRIMARY);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {
                // 得到联系人ID
                long contactId = phoneCursor.getLong(CONTACT_ID);
                String name = phoneCursor.getString(DISPLAY_NAME);
                String phontType = "";
                String number = "";
                if (LinkedHashMap.containsKey(name)) {
                    int phoneType = phoneCursor.getInt(TYPE);
                    phontType = bean.getType() + ";" + getPhoneTypeNameById(phoneType);
                    number = bean.getPhone() + ";" + phoneCursor.getString(NUMBER);
                } else {
                    int phoneType = phoneCursor.getInt(TYPE);
                    phontType = getPhoneTypeNameById(phoneType);
                    number = phoneCursor.getString(NUMBER);

                }
                ContactsEntity contactsEntity = new ContactsEntity();
                contactsEntity.setId(contactId);
                contactsEntity.setName(name);
                contactsEntity.setType(phontType);
                contactsEntity.setPhone(number.replace(" ", ""));
                contactsEntity.setFirstLater(phoneCursor.getString(PHONEBOOK_LABELINT));
                contactsEntity.setIconUri(phoneCursor.getString(PHOTO_URI));
                bean = contactsEntity;
                LinkedHashMap.put(contactsEntity.getName(), contactsEntity);
            }
            Collection<ContactsEntity> cl = LinkedHashMap.values();
            Iterator<ContactsEntity> it = cl.iterator();
            while (it.hasNext()) {
                lists.add(it.next());
            }
            phoneCursor.close();
        }
        return lists;
    }

    /**
     * 获取联系人信息速率最慢 多个cursor查询
     *
     * @return
     */
    public List<ContactsEntity> getAllContactsLow() {
        List<ContactsEntity> lists = new ArrayList<ContactsEntity>();
        ContentResolver resolver = mContext.getContentResolver();
        // 获取手机联系人
        Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.SORT_KEY_PRIMARY);
        if (phoneCursor != null) {
            while (phoneCursor.moveToNext()) {

                // 得到联系人ID
                long contactId = phoneCursor.getLong(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String phoneType = "";
                String phoneNumber = "";
                String photoUri = "";
                Cursor numberCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                        null,
                        ContactsContract.CommonDataKinds.Phone.CONTACT_ID + "="
                                + contactId, null, null);
                if (numberCursor != null) {
                    while (numberCursor.moveToNext()) {
                        photoUri = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.Contacts.PHOTO_URI));
                        System.out.println("ContactsManager.getAllContacts>>>" + photoUri);
                        int phoneTypeint = numberCursor.getInt(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE));

                        phoneType += getPhoneTypeNameById(phoneTypeint) + ";";
                        phoneNumber += numberCursor.getString(numberCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)) + ";";
                    }
                    numberCursor.close();
                }
                ContactsEntity contactsEntity = new ContactsEntity();
                contactsEntity.setId(contactId);
                contactsEntity.setName(phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)));
                contactsEntity.setType(phoneType);
                contactsEntity.setPhone(phoneNumber.replace(" ", ""));
                contactsEntity.setFirstLater(phoneCursor.getString(phoneCursor.getColumnIndex(PHONEBOOK_LABEL)));
                contactsEntity.setIconUri(photoUri);
                lists.add(contactsEntity);
            }
            removeDuplicateWithOrder(lists);
            phoneCursor.close();

        }
        return lists;
    }

    private String getPhoneTypeNameById(int typeId) {
        switch (typeId) {
            case ContactsContract.CommonDataKinds.Phone.TYPE_HOME:
                return "home";
            case ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE:
                return "mobile";
            case ContactsContract.CommonDataKinds.Phone.TYPE_WORK:
                return "work";
            case ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK:
                return "fax work";
            default:
                return "none";
        }
    }

    /*无序*/
    public void removeDuplicate(List arlList) {
        HashSet h = new HashSet(arlList);
        arlList.clear();
        arlList.addAll(h);
    }

    /*不改变原来顺序*/
    public static void removeDuplicateWithOrder(List<ContactsEntity> arlList) {
        Set set = new HashSet();
        List newList = new ArrayList();
        Iterator iter = arlList.iterator();
        while (iter.hasNext()) {
            ContactsEntity element = (ContactsEntity) iter.next();
            if (set.add(element.getId())) {
                newList.add(element);
            }
        }
        arlList.clear();
        arlList.addAll(newList);
    }

    /**
     * object类型必须重写equals方法和hashcode方法 再调用下面object方法
     * public class user{
     * String name;
     * int age;
     *
     * @param arlList
     * @Override public boolean equals(Object o){
     * if(o=this){return true};
     * if(!(o instanceof User)){
     * return false;
     * }
     * User user=(User)o;
     * //有几个比较几个
     * return user.name.equals(name)&&user.age==age;
     * }
     * @Override public int hashCode(){
     * //随便写
     * int result=11;
     * result=22*result+name.hashcode();
     * result=22*result+age;
     * return result;
     * }
     * }
     */
    public static void removeDuplicateWithOrderObject(List arlList) {
        Set set = new HashSet();
        List newList = new ArrayList();
        Iterator iter = arlList.iterator();
        while (iter.hasNext()) {
            Object element = iter.next();
            if (set.add(element) && set.contains(element)) {
                newList.add(element);
            }
        }
        arlList.clear();
        arlList.addAll(newList);
    }

    /*插入一个联系人*/
    public void insert(String name, String type, String number) {
        String[] str = number.split(";");
        ContentResolver resolver = mContext.getContentResolver();
        // 创建一个空的ContentValues
        ContentValues values = new ContentValues();
        // 向RawContacts.CONTENT_URI空值插入，
        // 先获取Android系统返回的rawContactId
        // 后面要基于此id插入值
        Uri rawContactUri = resolver.insert(ContactsContract.RawContacts.CONTENT_URI, values);
        long rawContactId = ContentUris.parseId(rawContactUri);
        values.clear();
        values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId);
        // 内容类型
        values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE);
        // 联系人名字
        values.put(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME, name);
        resolver.insert(ContactsContract.Data.CONTENT_URI, values);
        values.clear();
        int i = 0;
        while (i < str.length) {
            values.put(ContactsContract.RawContacts.Data.RAW_CONTACT_ID, rawContactId);
            // 内容类型
            values.put(ContactsContract.RawContacts.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            // 联系人的电话号码
            values.put(ContactsContract.CommonDataKinds.Phone.NUMBER, str[i]);
            // 电话类型
            values.put(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE);
            // 向联系人电话号码URI添加电话号码
            resolver.insert(ContactsContract.Data.CONTENT_URI, values);
            i++;
            values.clear();
        }
    }

    /*
     * 批量插入联系人1
     * 多个号码和类型用";"隔开类型和号码
     * 比如  moblie;work 123;234
     */
    public void insertOps1(List<ContactsEntity> list) {
        ContentResolver resolver = mContext.getContentResolver();
        int rawContactId = 0;//实现批量插入的关键
        ArrayList<ContentProviderOperation> ops = new ArrayList<>();
        int i = 0;
        while (i < list.size()) {
            rawContactId = ops.size();
            ops.add(ContentProviderOperation.newInsert(ContactsContract.RawContacts.CONTENT_URI)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, AccountManager.KEY_ACCOUNT_NAME)
                    .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, AccountManager.KEY_ACCOUNT_TYPE)  // 此处传入null添加一个raw_contact空数据
                    .build());
            ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                    .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)  // RAW_CONTACT_ID是第一个事务添加得到的，因此这里传入0，applyBatch返回的ContentProviderResult[]数组中第一项
                    .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                    .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, list.get(i).getName())
                    .build());
            String[] strPhone = list.get(i).getPhone().split(";");
            String[] strType = list.get(i).getType().split(";");

            for (int j = 0; j < strPhone.length; j++) {
                int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
                switch (strType[j]) {
                    case "home":
                        type = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                        break;
                    case "mobile":
                        type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

                        break;
                    case "work":
                        type = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
                        break;
                    case "fax work":
                        type = ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK;
                }
                ops.add(ContentProviderOperation.newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactId)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strPhone[j])
                        .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, type)
                        .build());
            }
            i++;
        }
        try {
            resolver.applyBatch(ContactsContract.AUTHORITY, ops);
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (OperationApplicationException e) {
            e.printStackTrace();
        }
    }

    /*
     * 批量插入联系人2
     * 多个号码和类型用";"隔开类型和号码
     * 比如  moblie;work 123;234
     */
    public void insertOps2(List<ContactsEntity> list) {
        ArrayList<ContentProviderOperation> ops = new ArrayList<ContentProviderOperation>();


        Iterator<ContactsEntity> it = null;
        if (list != null) {
            it = list.iterator();
        }

        int rawContactInsertIndex = 0;
        while (it != null && it.hasNext()) {
            ContactsEntity mv = it.next();
            rawContactInsertIndex = ops.size(); // 有了它才能给真正的实现批量添加
            if (mv.getPhone() != null) {
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.RawContacts.CONTENT_URI)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_TYPE, ACCOUNT_NAME)
                        .withValue(ContactsContract.RawContacts.ACCOUNT_NAME, ACCOUNT_TYPE)
                        .withYieldAllowed(true).build());
                // add name
                ops.add(ContentProviderOperation
                        .newInsert(ContactsContract.Data.CONTENT_URI)
                        .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                        .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)
                        .withValue(ContactsContract.CommonDataKinds.StructuredName.DISPLAY_NAME, mv.getName())
                        .withYieldAllowed(true).build());
                // add number
                String[] strPhone = mv.getPhone().split(";");
                String[] strType = mv.getType().split(";");
                for (int i = 0; i < strPhone.length; i++) {
                    int type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;
                    switch (strType[i]) {
                        case "home":
                            type = ContactsContract.CommonDataKinds.Phone.TYPE_HOME;
                            break;
                        case "mobile":
                            type = ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE;

                            break;
                        case "work":
                            type = ContactsContract.CommonDataKinds.Phone.TYPE_WORK;
                            break;
                        case "fax work":
                            type = ContactsContract.CommonDataKinds.Phone.TYPE_FAX_WORK;
                    }
                    ops.add(ContentProviderOperation
                            .newInsert(ContactsContract.Data.CONTENT_URI)
                            .withValueBackReference(ContactsContract.Data.RAW_CONTACT_ID, rawContactInsertIndex)
                            .withValue(ContactsContract.Data.MIMETYPE, ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE)
                            .withValue(ContactsContract.CommonDataKinds.Phone.NUMBER, strPhone[i])
                            .withValue(ContactsContract.CommonDataKinds.Phone.TYPE, ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE)
                            .withYieldAllowed(true).build());
                }
            }
        }
        ContentProviderResult[] results = null;
        if (ops != null) {
            try {
                results = mContext.getContentResolver()
                        .applyBatch(ContactsContract.AUTHORITY, ops);
                Toast.makeText(mContext,"Successful",Toast.LENGTH_SHORT).show();
            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }

        }
    }
}
