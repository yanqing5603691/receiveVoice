package com.example.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.tencentcloudapi.asr.v20190614.AsrClient;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskRequest;
import com.tencentcloudapi.asr.v20190614.models.CreateRecTaskResponse;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusRequest;
import com.tencentcloudapi.asr.v20190614.models.DescribeTaskStatusResponse;
import com.tencentcloudapi.common.Credential;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import com.tencentcloudapi.common.profile.ClientProfile;
import com.tencentcloudapi.common.profile.HttpProfile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * Created by yanqing on 2020/2/28.
 */
public class tencentCloudByUrl {
    static String SecretId = "AKIDXKsCH7ilaAoDOJlWzDBtxe8Ni6GBdCMI";
    static String SecretKey = "qhJhqiT8aonbNuBf4FAoUv79OZVBeqmT";
    public static String putVoiceByUrl(String url){
        try{
            Credential cred = new Credential(SecretId,SecretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            AsrClient client = new AsrClient (cred,"ap-shanghai",clientProfile);
            String urlPath = "\""+url+"\"";
            String params = "{\"EngineModelType\":\"16k_0\",\"ChannelNum\":1,\"ResTextFormat\":0,\"SourceType\":0,\"Url\":"+urlPath+"}";
            CreateRecTaskRequest req = CreateRecTaskRequest .fromJsonString(params,CreateRecTaskRequest .class);
            CreateRecTaskResponse resp = client.CreateRecTask(req);
            System.out.println(CreateRecTaskRequest.toJsonString(resp));
            return CreateRecTaskRequest.toJsonString(resp);
            //System.out.println(resp);
        }catch (TencentCloudSDKException e){
            System.out.println(e.toString());
            return "false";
        }
    }

    public static String putBylocaleFile(String filePath) throws IOException {
        try{
            Credential cred = new Credential(SecretId,SecretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            ClientProfile  clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            AsrClient client = new AsrClient(cred,"ap-shanghai",clientProfile);
            String params = "{\"EngineModelType\":\"16k_0\",\"ChannelNum\":1,\"ResTextFormat\":0,\"SourceType\":1}";
            CreateRecTaskRequest req = CreateRecTaskRequest.fromJsonString(params,CreateRecTaskRequest.class);
            File file = new File(filePath);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] buffer = new byte[(int)file.length()];
            req.setDataLen(file.length());
            inputStream.read(buffer);
            inputStream.close();
            String encodeData = Base64.getEncoder().encodeToString(buffer);
            req.setData(encodeData);
            CreateRecTaskResponse resp = client.CreateRecTask(req);
            System.out.println(CreateRecTaskRequest.toJsonString(resp));
            return CreateRecTaskRequest.toJsonString(resp);
        }catch (TencentCloudSDKException e){
            System.out.println(e.toString());
            return "false";
        }
    }

    public static String findResultByTaskId(Integer TaskId){
        try{
            Credential cred = new Credential(SecretId,SecretKey);
            HttpProfile httpProfile = new HttpProfile();
            httpProfile.setEndpoint("asr.tencentcloudapi.com");
            ClientProfile clientProfile = new ClientProfile();
            clientProfile.setHttpProfile(httpProfile);
            AsrClient client = new AsrClient(cred,"ap-shanghai",clientProfile);
            String params = "{\"TaskId\":"+TaskId+"}";
            DescribeTaskStatusRequest req = DescribeTaskStatusRequest.fromJsonString(params,DescribeTaskStatusRequest.class);
            DescribeTaskStatusResponse resp = client.DescribeTaskStatus(req);
            //System.out.println(DescribeTaskStatusRequest.toJsonString(resp));
            return DescribeTaskStatusRequest.toJsonString(resp);
        }catch (TencentCloudSDKException e){
            System.out.println(e.toString());
            return "false";
        }
    }

    public static void main(String [] args)throws IOException{
        String stutuStr = "false";
        String result = null;
        Integer IntTaskId = null;
        String url = "http://ttsgz-1255628450.cos.ap-guangzhou.myqcloud.com/20190813/cbf318cd-273e-4b7c-bab0-50a1885c9b96.wav";
        //String localePath = D:\\Voice\38db9e2a-fbbe-11e9-ae71-0050568b2cb9-1207379289.wav";
        String localePath = "D:\\Voice\\38db9e2a-fbbe-11e9-ae71-0050568b2cb9-1207379289.wav";
        String TaskId = null;
//        if(url != null && !url.equals("")){
//            TaskId = putVoiceByUrl(url);
//            JSONObject jsonObject = JSONObject.parseObject(TaskId);
//            JSONObject jsonObjectId = JSONObject.parseObject(jsonObject.get("Data").toString());
//            System.out.println(jsonObject.get("Data"));
//            System.out.println(jsonObjectId.get("TaskId"));
//            IntTaskId = (Integer) jsonObjectId.get("TaskId");
//        }
        if(localePath != null && !localePath.equals("")){
            TaskId = putBylocaleFile(localePath);
            JSONObject jsonObject = JSONObject.parseObject(TaskId);
            JSONObject jsonObjectId = JSONObject.parseObject(jsonObject.get("Data").toString());
            System.out.println(jsonObject.get("Data"));
            System.out.println(jsonObjectId.get("TaskId"));
            IntTaskId = (Integer) jsonObjectId.get("TaskId");
        }

        while (!stutuStr.equalsIgnoreCase( "success")){
            String jsonValue = findResultByTaskId(IntTaskId);
            JSONObject  jsonData = JSONObject.parseObject(jsonValue);
            JSONObject jsonObjectData = JSONObject.parseObject(jsonData.get("Data").toString());
            stutuStr = jsonObjectData.get("StatusStr").toString();
            result = jsonObjectData.get("Result").toString();
            if(stutuStr.equalsIgnoreCase("success")){
                System.out.println(stutuStr);
                System.out.println(result);
                break;
            }
        }


        //JSONObject jsonObjectStatuStr = JSONObject.parseObject(jsonObjectData.get("StatuStr").toString());
        //JSON json = JSON.parse(jsonValue);


    }
}
