package com.mny.contactdemo;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Crate by E470PD on 2018/8/27
 */
public class IoUtils {
    public IoUtils(){}
    //文件路径
    String path = Environment.getExternalStorageDirectory().toString()
            + "/myphone.txt";
    public void write(String str){
        Log.e("myTag", "write: "+path);
        //判断文件是否存在
        File file = new File(path);
        if (file.exists()) {
            Log.e("myTag", "文件存在");
        } else {
            try {
                if (file.createNewFile()) {
                    Log.e("myTag", "文件创建成功");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        try {
            /* 写入Txt文件 */
            File writename = new File(path); // 相对路径，如果没有则要建立一个新的output。txt文件
            writename.createNewFile(); // 创建新文件
            BufferedWriter out = new BufferedWriter(new FileWriter(writename));
            out.write(str); // \r\n即为换行
            out.flush(); // 把缓存区内容压入文件
            out.close(); // 最后记得关闭文件
            Log.e("myTag", "json数据保存到成功！！！");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public String read(){
        //判断文件是否存在
        File file = new File(path);
        if (file.exists()) {
            Log.e("myTag", "文件存在");
        } else {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.e("myTag", "文件创建成功");
        }

        try {
            File filename = new File(path); // 要读取以上路径的input。txt文件
            InputStreamReader reader = new InputStreamReader(
                    new FileInputStream(filename)); // 建立一个输入流对象reader
            BufferedReader br = new BufferedReader(reader); // 建立一个对象，它把文件内容转成计算机能读懂的语言
            String line = "";
            StringBuffer buffer=new StringBuffer();
            while (( line = br.readLine())  != null) {
                buffer.append(line);// 一次读入一行数据
            }
            reader.close();
            return buffer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "错误";
        }
    }
}
