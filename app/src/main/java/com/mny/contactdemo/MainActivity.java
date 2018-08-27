package com.mny.contactdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity {
    ContactsManager manager;
    long time;
    TextView tv;
    List<ContactsEntity> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        manager = new ContactsManager(this);
        super.onCreate(savedInstanceState);
//        插入联系人
//        manager.insertOps1(insertList());
//        manager.insertOps2(insertList());
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.tv);
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
                            }
                        });
                    }
                }).start();
            }
        });

    }

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
}
