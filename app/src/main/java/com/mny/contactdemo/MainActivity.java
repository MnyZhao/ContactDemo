package com.mny.contactdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * 根目录下myphone.txt文件
 */
public class MainActivity extends Activity {
    ContactsManager manager;
    JsonUtils jsonUtils;
    long time;
    TextView tv;
    EditText et;
    List<ContactsEntity> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = new ContactsManager(this);
        jsonUtils = new JsonUtils();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /*测试插入电话*/
//        manager.insertOps1(insertList());
        tv = (TextView) findViewById(R.id.tv);
        et = (EditText) findViewById(R.id.et);
        findViewById(R.id.btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (list != null) {
                            list.clear();
                        }
                        time = System.currentTimeMillis();
                        list = manager.getAllContacts();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long times = System.currentTimeMillis() - time;
                                StringBuilder str = new StringBuilder();
                                for (int i = 0; i < list.size(); i++) {
                                    str.append(list.get(i).toString() + "\n");
                                }
                                str.append("ms" + times);
                                tv.setText(str);
                                et.setText(jsonUtils.listToJson(list));
                                /*写入文件*/
                                write(jsonUtils.listToJson(list));
                            }
                        });
                    }
                }).start();
            }
        });
        findViewById(R.id.default_low).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        if (list != null) {
                            list.clear();
                        }
                        time = System.currentTimeMillis();
                        list = manager.getAllContactsLow();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                long times = System.currentTimeMillis() - time;
                                StringBuilder str = new StringBuilder();
                                for (int i = 0; i < list.size(); i++) {
                                    str.append(list.get(i).toString() + "\n");
                                }
                                str.append("ms" + times);
                                tv.setText(str);
                                et.setText(jsonUtils.listToJson(list));
                                /*写入文件*/
                                write(jsonUtils.listToJson(list));
                            }
                        });
                    }
                }).start();
            }
        });
        findViewById(R.id.btn_write).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, WriteActivity.class);
                startActivity(intent);
                finish();
            }
        });

    }

    /*测试插入电话获取电话列表*/
    private List<ContactsEntity> insertList() {
        List<ContactsEntity> list = new ArrayList<>();

        for (int i = 0; i < 7; i++) {
            ContactsEntity contactsEntity = new ContactsEntity();
            contactsEntity.setName("A" + i);
            contactsEntity.setPhone("1171127" + i + ";" + "12431412347" + i);
            contactsEntity.setType("mobile;moblie");
            list.add(contactsEntity);
        }
        return list;
    }

    /*写入文件*/
    private void write(final String str) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                IoUtils ioUtils = new IoUtils();
                ioUtils.write(str);
            }
        }).start();
        Toast.makeText(MainActivity.this, "电话文件保存成功", Toast.LENGTH_SHORT).show();
    }

}
