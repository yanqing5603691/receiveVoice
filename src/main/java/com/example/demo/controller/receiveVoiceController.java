package com.example.demo.controller;

import com.example.demo.model.AnalyzeJsonInfo;
import com.example.demo.model.ReceiveVoice;
import com.example.demo.model.VoiceJsonInfo;
import com.example.demo.util.GetCreateTime;
import com.example.demo.util.GetLength;
import com.example.demo.util.Result;
import com.example.demo.util.getStartUtil;
import com.google.gson.Gson;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by Yan Qing on 2019/8/6.
 */
@Controller
public class receiveVoiceController {
    private final Logger logger = LoggerFactory.getLogger(receiveVoiceController.class);
    @Value("${savePath}")
    String savePath;

    @Value("${jsonSavePath}")
    String jsonSavePath;

    @Value("${mediaPath}")
    String mediaPath;

    @Value("${txtPath}")
    String txtPath;

    @Value("${jsonPath}")
    String jsonPath;


    @RequestMapping(value = "receiveVoice")
    @ResponseBody
    public ResponseEntity receiveVoice(@RequestBody ReceiveVoice receiveVoice) {
        if(!getStartUtil.getFlag()){
            return new ResponseEntity("date_overTime", HttpStatus.LOOP_DETECTED);
        }
        // String savePath = "C:/Voice/";
        String sessionid = receiveVoice.getSessionid();
        String chargeNbr = receiveVoice.getChargeNbr();
        //receiveVoice.setVoiceAddress(receiveVoice.getVoiceAddress());
        //receiveVoice.setVoiceAddress("https://gss0.baidu.com/9fo3dSag_xI4khGko9WTAnF6hhy/wenku/q%3D90%3Bw%3D500/sign=a01a2ff4ac0f4bfb8ad09254383845c5/35a85edf8db1cb136b4c7c78d554564e92584bb7.jpg");
        String voiceAddress = receiveVoice.getVoiceAddress();
//        //获取项目路径media地址
//        String path= null;		//获取临时文件路径
//        try {
//            path = File.createTempFile("datas", ".txt").getPath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        File file=new File(path).getParentFile();		//获取临时文件存放的文件夹
//        File[] files=file.listFiles();				//取文件夹下所有文件
//        File f = files[0]; 				//遍历删除所有文件
//        //String aPath = f.getAbsolutePath();
//        String tomcat_save_path = f.getParentFile().getParent()+mediaPath;
//        String cope_to_tomcat = downloadByUrl(voiceAddress,tomcat_save_path);
        String VoiceName = downloadByUrl(voiceAddress, savePath);
        createJson(receiveVoice, VoiceName);
        return new ResponseEntity("success", HttpStatus.OK);
    }

    public void createJson(ReceiveVoice receiveVoice, String VoiceName) {
        String sessionId = getSessionId(receiveVoice.getVoiceAddress());
        VoiceJsonInfo voiceJson = new VoiceJsonInfo();
        voiceJson.callreference = receiveVoice.getSessionid();
        voiceJson.calledid = receiveVoice.getCalledNbr();
        voiceJson.callerid = receiveVoice.getCallerNbr();
        voiceJson.extension = voiceJson.callerid;
        Long length = null;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date createDate = null;
        int index = VoiceName.lastIndexOf("_");
        index = VoiceName.lastIndexOf("_", index - 1);
        index = VoiceName.lastIndexOf("_", index - 1);
        String time_str = VoiceName.substring(index + 1, VoiceName.length());
        String createTime_str = time_str.substring(0, 4) + "-" + time_str.substring(4, 6) + "-" + time_str.substring(6, 8) + " " + time_str.substring(9, 11) + ":" + time_str.substring(12, 14) + ":" + time_str.substring(14, 16);
        try {
            createDate = sdf.parse(createTime_str);
        } catch (Exception e) {
            logger.info("日期转换异常" + new Date());
        }
        String fileFlagName = receiveVoice.getVoiceAddress().substring(receiveVoice.getVoiceAddress().lastIndexOf("/"), receiveVoice.getVoiceAddress().length());
        String flag = fileFlagName.substring(fileFlagName.length() - 6, fileFlagName.length() - 4);
        if (!"ut".equalsIgnoreCase(flag) && !"in".equalsIgnoreCase(flag)) {
            length = GetLength.getLengthByPath(savePath + sessionId+".wav");
            voiceJson.duration = length.intValue();
            voiceJson.cleartime = GetCreateTime.secondeToDate((createDate.getTime() + length * 1000) / 1000, "yyyy-MM-dd HH:mm:ss");
            voiceJson.cleartick = createDate.getTime() / 1000 + length;
        }
        voiceJson.createtime = createTime_str;
        //String getStartSecondsNumber = String.valueOf(createDate.getTime()*1000);
        voiceJson.createtick = createDate.getTime() / 1000;
        voiceJson.connecttime = voiceJson.createtime;
        voiceJson.connecttick = voiceJson.createtick;
        JSONArray jsonArray = new JSONArray();
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("rootdir", savePath);
        jsonObject.put("shortName0", sessionId+"_shortName0.wav");
        jsonObject.put("shortName1", sessionId+"_shortName1.wav");
        jsonObject.put("startRecordtime", voiceJson.createtick * 1000);
        jsonObject.put("stopRecordtime", voiceJson.cleartick * 1000);
        jsonObject.put("type", "1");
        jsonArray.add(jsonObject);
        voiceJson.audioList = jsonArray;
        String fileName = jsonSavePath + "server6." + receiveVoice.getSessionid() + "_rs";
        if (!"ut".equalsIgnoreCase(flag) && !"in".equalsIgnoreCase(flag)) {
            saveJsonToFile(Serializer(voiceJson), fileName);
            logger.info("Save Json To DCLog Success"+new Date());
        }
        //存储转写信息到analyze_json中
        AnalyzeJsonInfo analyzeJsonInfo = new AnalyzeJsonInfo();
        analyzeJsonInfo.calledId = voiceJson.calledid;
        analyzeJsonInfo.callerId = voiceJson.calledid;
        analyzeJsonInfo.extension = voiceJson.extension;
        analyzeJsonInfo.startTime = voiceJson.connecttime;
        analyzeJsonInfo.callReference = voiceJson.callreference;
        analyzeJsonInfo.voicePath = receiveVoice.getVoiceAddress();
        analyzeJsonInfo.duration = voiceJson.duration;
        String analyzeJsonFileName = jsonPath + analyzeJsonInfo.callReference;
        if ("ut".equalsIgnoreCase(flag)) {
            analyzeJsonInfo.role = "AGENT";
            saveJsonToFile(Serializer(analyzeJsonInfo), analyzeJsonFileName+"_shortName0");
            logger.info("Save Json To XML Success"+new Date());
        }else if("in".equalsIgnoreCase(flag)){
            analyzeJsonInfo.role = "USER";
            saveJsonToFile(Serializer(analyzeJsonInfo), analyzeJsonFileName+"_shortName1");
            logger.info("Save Json To XML Success"+new Date());
        }else {
            logger.info("******执行完成*****"+new Date());
        }
        //method1(voiceAddress+":"+Serializer(voiceJson));
        logger.info(VoiceName + "******write_to_json success  " + new Date());
    }
    public String getSessionId(String url){
        String fileName = url.substring(url.lastIndexOf("/"), url.length());
        int startIndex = fileName.indexOf("_");
        startIndex = fileName.indexOf("_", startIndex + 1);
        startIndex = fileName.indexOf("_", startIndex + 1);
        int endIndex = fileName.lastIndexOf("_");
        endIndex = fileName.lastIndexOf("_", endIndex - 1);
        endIndex = fileName.lastIndexOf("_", endIndex - 1);
        String sessionId = fileName.substring(startIndex + 1, endIndex);
        return sessionId;
    }

    public String downloadByUrl(String url, String savePath) {
        String fileName = url.substring(url.lastIndexOf("/"), url.length());
        try {
            File txtFile = ResourceUtils.getFile("classpath:static/media/writeUrl.txt");
            method2(txtFile, fileName);
            logger.info("写入WriteUrl.txt文本成功"+new Date());
        } catch (FileNotFoundException e) {
            logger.info("写入WriteUrl.txt文本异常"+new Date());
            e.printStackTrace();
        }
        String sessionId = getSessionId(url);
        String flag = fileName.substring(fileName.length() - 6, fileName.length() - 4);
        String state = null;
        if ("ut".equalsIgnoreCase(flag)) {
            state = "_shortName0";
        } else if ("in".equalsIgnoreCase(flag)) {
            state = "_shortName1";
        } else {
            state = "";
        }
        String newFileName = sessionId + state + ".wav";
        File file = new File(savePath);
        if (!file.exists()) {
            file.mkdirs();
        }
        FileOutputStream fileOutputStream = null;
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        try {
            URL httpUrl = new URL(url);
            conn = (HttpURLConnection) httpUrl.openConnection();
            //conn.setRequestMethod("get");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.connect();
            inputStream = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(inputStream);
            //判断文件的保存路径后面是否以/结尾
            if (!savePath.endsWith("/")) {

                savePath += "/";

            }
            fileOutputStream = new FileOutputStream(savePath + newFileName);
            BufferedOutputStream bos = new BufferedOutputStream(fileOutputStream);
            byte[] buf = new byte[4096];
            int length = bis.read(buf);
            while (length != -1) {
                bos.write(buf, 0, length);
                length = bis.read(buf);
            }
            bos.close();
            bis.close();
            conn.disconnect();
            logger.info("DownLoad success  " + new Date());
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("下载失败，抛出异常!!!!!!");
            return null;
        }
        return fileName.replace("/", "");
    }


    public static String Serializer(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public boolean saveJsonToFile(String json, String savePath) {
        try {
            File file1 = new File(jsonSavePath);
            if (!file1.exists()) {
                file1.mkdirs();
            }
            RandomAccessFile file = new RandomAccessFile(savePath, "rw");
            file.setLength(0);
            file.write(json.getBytes());
            file.close();
        } catch (Exception e) {
            logger.info("写入json失败");
            return false;
        }
        logger.info("写入json成功");
        return true;
    }


    public static void method2(File file, String content) {
        BufferedWriter out = null;
        try {
            out = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(file, true)));
            out.write(content + "\r\n");
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }
}
