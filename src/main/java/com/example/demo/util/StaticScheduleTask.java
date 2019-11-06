package com.example.demo.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Date;

/**
 * Created by yan qing on 2019/5/22.
 */
@Component
@Configuration
@EnableScheduling
public class StaticScheduleTask {
    private static final Logger LOGGER = LoggerFactory.getLogger(StaticScheduleTask.class);
    @Value("${xmlPath}")
      String xmlPath;

    @Async
    @Scheduled(cron = "0/10 * * * * ?")
    private void getFileNameTasks() throws IOException {
        InvokeBat invokeBat = new InvokeBat();
        invokeBat.runBat("getFileName.bat");
        invokeBat.runBat("getXmlName.bat");
    }

    @Async
    @Scheduled(cron = "0 */2 * * * ?")
    private void startExeTasks() throws IOException {
        InvokeBat invokeBat = new InvokeBat();
        //invokeBat.runBat("startAnalysisVoiceExe.bat");
        invokeBat.runBat("startVbs.bat");

    }

    @Async
    @Scheduled(cron = "0/40 * * * * ?")
    private void submitToES(){
        if(!getStartUtil.getFlag()){
            LOGGER.error("已经失效！！！");
            return;
        }
        try {
            BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(xmlPath)),"UTF-8"));
            String lineText = null;
            while ((lineText = br.readLine())!= null){
                String[] xmlNames = lineText.split(",");
                String localName = null;
                for (String name :xmlNames){
                    String thisName = name.substring(0,name.lastIndexOf("_"));
                    if(thisName == localName || thisName == null){
                        return;
                    }
                    localName = thisName;
                    String shortName0 = name.substring(0,name.lastIndexOf("_"))+"_shortName0";
                    String shortName1 = name.substring(0,name.lastIndexOf("_"))+"_shortName1";
                    //String msg = ESConnectToSubmit.submitXmlToES(name);
                    String msg = ESConnectToSubmit.submitTransInfoToES(shortName0,shortName1);
                    if (msg.equalsIgnoreCase("success")){
                        deleteFile(shortName0);
                        deleteFile(shortName1);
                    }else {
                        LOGGER.info("上传单个文件到ES" + name + "失败！"+new Date());
                    }
                }
            }
            br.close();
        } catch (Exception e) {
            LOGGER.error("文件名未找到");
        }

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
