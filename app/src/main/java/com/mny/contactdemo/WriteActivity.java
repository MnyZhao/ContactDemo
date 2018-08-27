package com.mny.contactdemo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class WriteActivity extends Activity {
    EditText etShow;
    Button btnWrite,btnRead;
    JsonUtils jsonUtils;
    ContactsManager contactsManager;
    String str;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        jsonUtils=new JsonUtils();
        contactsManager=new ContactsManager(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        init();
    }

    private void init() {
        etShow= (EditText) findViewById(R.id.et_show);
        btnWrite= (Button) findViewById(R.id.write);
        btnRead= (Button) findViewById(R.id.read);
        btnRead.setOnClickListener(listener);
        btnWrite.setOnClickListener(listener);
    }
    View.OnClickListener listener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.write:
                    if (!etShow.getText().toString().isEmpty()) {
                        contactsManager.insertOps1(jsonUtils.jsonToList(etShow.getText().toString()));
                        Toast.makeText(WriteActivity.this,"写入成功",Toast.LENGTH_SHORT).show();
                    }else{
                        Toast.makeText(WriteActivity.this,"请读取电话文件(从上个手机中复制过来即可)",Toast.LENGTH_SHORT).show();
                    }
                    break;
                case R.id.read:
                    Toast.makeText(WriteActivity.this,"读取中稍后....",Toast.LENGTH_SHORT).show();
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            IoUtils ioUtils=new IoUtils();
                            str=ioUtils.read();
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(WriteActivity.this,"读取成功",Toast.LENGTH_LONG).show();
                                    etShow.setText(str);
                                }
                            });
                        }
                    }).start();
                    break;
            }
        }
    };
}
