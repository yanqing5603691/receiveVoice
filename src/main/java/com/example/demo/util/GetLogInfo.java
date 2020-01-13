package com.example.demo.util;


import java.io.File;
import java.io.RandomAccessFile;
import java.util.Date;

/**
 * Created by yanqing on 2019/12/2.
 */
public class GetLogInfo {
    public static long pointer = 0;

    public static void randomRedLog(String path){
        try{
            File file = new File(path);
            if(file ==  null){
                System.out.println("文件不存在!!!"+new Date());
                return;
            }
            RandomAccessFile raf = new RandomAccessFile(file, "r");
            System.out.println("当前文件指针的初始位置:"+raf.getFilePointer());
            raf.seek(pointer);
            String line = null;
            while((line = raf.readLine()) != null ){
                if(line.equals("")){
                    continue;
                }
                line = new String(line.getBytes("ISO-8859-1"),"UTF-8");
                System.out.println("line:"+line);
            }
            pointer = raf.getFilePointer();

        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }

    public static void main(String[] agrs){
        String path = "";
        randomRedLog(path);
    }
}
