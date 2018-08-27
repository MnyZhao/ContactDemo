package com.mny.contactdemo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Crate by E470PD on 2018/8/27
 */
public class JsonUtils {
    public JsonUtils(){

    }
    public String listToJson(List<ContactsEntity> list) {
        try {
            JSONArray jsonArray = new JSONArray();
            for (int i = 0; i < list.size(); i++) {
                JSONObject object = new JSONObject();
                ContactsEntity cn = list.get(i);
                object.put("name", cn.getName());
                object.put("phone", cn.getPhone());
                object.put("type", cn.getType());
                jsonArray.put(object);
            }
            return jsonArray.toString();
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
    public List<ContactsEntity> jsonToList(String str){
        List<ContactsEntity> list=new ArrayList<>();
        try {
            JSONArray jsonArray=new JSONArray(str);
            for(int i=0;i<jsonArray.length();i++){
                JSONObject jsonObject= (JSONObject) jsonArray.get(i);
                ContactsEntity contactsEntity=new ContactsEntity();
                contactsEntity.setName(jsonObject.getString("name"));
                contactsEntity.setPhone(jsonObject.getString("phone"));
                contactsEntity.setType(jsonObject.getString("type"));
                list.add(contactsEntity);
            }
            return list;
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
