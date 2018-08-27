package com.mny.contactdemo;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by MnyZhao on 2017/12/5.
 */

public class ContactsEntity implements Parcelable, Serializable {
    //id
    private long id;
    //name
    private String name;
    //号码 可能有多个
    private String phone;
    //头像uri
    private String iconUri;
    //拼音
    private String pinyin;
    //首字母
    private String firstLater;
    //电话号码类型（家庭、工作）
    // ContactsContract.CommonDataKinds.Phone.TYPE_HOME、TYPE_MOBILE、TYPE_WORK、TYPE_FAX_WORK
    private String type;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getIconUri() {
        return iconUri;
    }

    public void setIconUri(String iconUri) {
        this.iconUri = iconUri;
    }

    public String getPinyin() {
        return pinyin;
    }

    public void setPinyin(String pinyin) {
        this.pinyin = pinyin;
    }

    public String getFirstLater() {
        return firstLater;
    }

    public void setFirstLater(String firstLater) {
        this.firstLater = firstLater;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.id);
        dest.writeString(this.name);
        dest.writeString(this.phone);
        dest.writeString(this.iconUri);
        dest.writeString(this.pinyin);
        dest.writeString(this.firstLater);
        dest.writeString(this.type);
    }

    public ContactsEntity() {
    }

    protected ContactsEntity(Parcel in) {
        this.id = in.readLong();
        this.name = in.readString();
        this.phone = in.readString();
        this.iconUri = in.readString();
        this.pinyin = in.readString();
        this.firstLater = in.readString();
        this.type = in.readString();
    }

    public static final Parcelable.Creator<ContactsEntity> CREATOR = new Parcelable.Creator<ContactsEntity>() {
        @Override
        public ContactsEntity createFromParcel(Parcel source) {
            return new ContactsEntity(source);
        }

        @Override
        public ContactsEntity[] newArray(int size) {
            return new ContactsEntity[size];
        }
    };

    @Override
    public String toString() {
        return getId() + "    name>>>  " + getName() + "    phone>>>  " + getPhone() + "    URl>>>>>" + getIconUri() + "    booklaebl>>>    " + getFirstLater() + "   type>>>>>>>>" + getType();
    }
}
