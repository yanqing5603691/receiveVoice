package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;

/**
 * Created by yan qing on 2019/5/21.
 */
public class InvokeBat {
    private static final Logger LOGGER = LoggerFactory.getLogger(InvokeBat.class);

    public void runBat(String batName) throws IOException {
        String cmd = "D:\\Voice\\txt"+"\\"+batName;
        try {
            Process ps = Runtime.getRuntime().exec(cmd);
            InputStream in = ps.getInputStream();
            int c;
            while ((c = in.read()) != -1){
                //System.out.println(c);
            }
            in.close();
            ps.waitFor();
            LOGGER.info(batName+"------"+new Date()+"start bat successful");
        }catch (IOException ioe){
            ioe.printStackTrace();
            LOGGER.error(batName+"-----"+new Date()+"start bat failed");
        }catch (InterruptedException e){
            e.printStackTrace();
            LOGGER.error(batName+"-----"+new Date()+"start bat failed");
        }
        //System.out.println(batName+"------"+new Date()+"start bat successful");

}

    public static boolean deleteFile(String fileName) {
        File file = new File(fileName);
        // 如果文件路径所对应的文件存在，并且是一个文件，则直接删除
        if (file.exists() && file.isFile()) {
            if (file.delete()) {
                LOGGER.info("删除单个文件" + fileName + "成功！"+new Date());
                return true;
            } else {
                LOGGER.error("删除单个文件" + fileName + "失败！"+new Date());
                return false;
            }
        } else {
            LOGGER.error("删除单个文件失败：" + fileName + "不存在！"+new Date());
            return false;
        }
    }
}
