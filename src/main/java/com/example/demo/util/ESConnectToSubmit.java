package com.example.demo.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.model.Sentence;
import com.example.demo.model.TransData;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.List;

/**
 * Created by yanqing on 2019/10/15.
 */
@Component
public class ESConnectToSubmit  {
    @Value("${es.hostUrl}")
    public void setEsHostUrl(String esHostUrl) {
        ESConnectToSubmit.esHostUrl = esHostUrl;
    }
    @Value("${es.port}")
    public void setEsPort(int esPort) {
        ESConnectToSubmit.esPort = esPort;
    }
    @Value("${es.userName}")
    public void setEsUserName(String esUserName) {
        ESConnectToSubmit.esUserName = esUserName;
    }
    @Value("${es.passWord}")
    public void setEsPassWord(String esPassWord) {
        ESConnectToSubmit.esPassWord = esPassWord;
    }
    @Value("${es.search.base.path}")
    public void setEsBasePath(String esBasePath) {
        ESConnectToSubmit.esBasePath = esBasePath;
    }
    static String esHostUrl;
    static int esPort;
    static String esUserName;
    static String esPassWord;
    static String esBasePath;



    private static final Logger logger = LoggerFactory.getLogger(ESConnectToSubmit.class);
    public static RestHighLevelClient esClient;
    public static String es_index,es_type;//提交es时使用的路径信息


    public static String submitXmlToES(String transFile) throws Exception {
            try{
                //System.out.println(esUserName+"**********"+esPassWord);
                //初始化ES操作客户端
                CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(esUserName,esPassWord));
                esClient =new RestHighLevelClient(
                        RestClient.builder(
                                new HttpHost(esHostUrl, esPort)
                        ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                            public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                                httpClientBuilder.disableAuthCaching();
                                return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                            }
                        })/*.setMaxRetryTimeoutMillis(2000)*/
                );
                String esSearchBasePath = "";
                if(esBasePath==null){
                    esSearchBasePath="iccm/quality_record/";
                }else{
                    esSearchBasePath = esBasePath;
                }
                if(esSearchBasePath.contains("/")){
                    es_index = esSearchBasePath.substring(0,esSearchBasePath.indexOf("/"));
                    String temp_type = esSearchBasePath.substring(esSearchBasePath.indexOf("/"));
                    if(temp_type!=null){
                        String[] typeCells = temp_type.split("/");
                        StringBuilder sb = new StringBuilder();
                        for (String typeCell : typeCells) {
                            if(!"".equals(typeCell))
                                sb.append(typeCell).append("/");
                        }
                        es_type = sb.toString().substring(0,sb.lastIndexOf("/"));
                    }
                }else{
                    es_index = esSearchBasePath;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

        if(transFile == null){
            logger.info("提交失败，没有指定的转写文件");
            return "error";
        }
        //获取转写结果数据对象
        TransData transData = DataUtil.getTransDataFromFile(transFile);
        List<Sentence> sentenceList = transData.getSentenceList();
        //写入ES
        /*组装保存到ES中的信息*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ESUtil esUtil = ESUtil.getInstance(esHostUrl);
        JSONObject jsonObj = new JSONObject();//用来封装保存到ES服务中的对象
        jsonObj.put("duration", transData.getDuration()*1000);
        jsonObj.put("direction",transData.getDir());
        jsonObj.put("orig_reference", transData.getCallReference());
        jsonObj.put("media_from", transData.getCallerId());
        jsonObj.put("media_to", transData.getCalledId());
        jsonObj.put("start_time", sdf.format(transData.getStartTime()));
        jsonObj.put("extension", transData.getExtension());
        jsonObj.put("media_type", "00");
        jsonObj.put("media_platform", "001");
        JSONArray fragmentsArr = new JSONArray();
        // 读取转写的文字信息
        StringBuilder agentTxtSB = new StringBuilder();
        StringBuilder customerTxtSB = new StringBuilder();
        if(sentenceList!=null){
            for (Sentence sentence : sentenceList) {
                if("AGENT".equals(sentence.getRole())){//坐席说的话
                    agentTxtSB.append(sentence.getText()+" ");
                }else{//用户说的
                    customerTxtSB.append(sentence.getText()+" ");
                }
                JSONObject cellJson = new JSONObject();
                cellJson.put("from",sentence.getRole());
                cellJson.put("begin",sentence.getBegin());
                cellJson.put("end",sentence.getEnd());
                cellJson.put("text",sentence.getText());
                fragmentsArr.add(cellJson);
            }
        }
        jsonObj.put("agent_text",agentTxtSB.toString());
        jsonObj.put("customer_text",customerTxtSB.toString());
        jsonObj.put("fragments",fragmentsArr);
        StringBuilder referenceSB= new StringBuilder();
        referenceSB.append("Q").append(jsonObj.getString("media_type"))
                .append(jsonObj.getString("media_platform"))
                .append(transData.getCallReference());

        String path=es_index+"/"+es_type+"/"+referenceSB.toString();
        System.out.println("---提交路径:"+path);
        logger.info("---提交路径:"+path);
        logger.info(jsonObj.toJSONString());

        IndexRequest request = new IndexRequest(es_index, es_type, referenceSB.toString());
        request.source(jsonObj, XContentType.JSON);
        IndexResponse response = esClient.index(request);
        int statusCode = response.status().getStatus();
//        boolean flag = esUtil.createOrUpdateData(basicConfig.esSearchBasePath+referenceSB.toString(),jsonObj);
        logger.info("提交ES,返回的状态码:{},结果:{}",statusCode,response.getResult().getLowercase());
        if(statusCode==200||statusCode==201){
            logger.info("------"+transData.getCallReference()+":提交ES完成--------");
            return "success";
        }else{
            logger.error("提交ES操作出错,错误结果:"+response.getResult().getLowercase());
            logger.error("提交ES失败,失败录音:"+transData.getCallReference());
            return "error";
        }
    }

    public static String submitTransInfoToES(String transFile1, String transFile2) throws Exception {
        try{
            //System.out.println(esUserName+"**********"+esPassWord);
            //初始化ES操作客户端
            CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
            credentialsProvider.setCredentials(AuthScope.ANY,
                    new UsernamePasswordCredentials(esUserName,esPassWord));
            esClient =new RestHighLevelClient(
                    RestClient.builder(
                            new HttpHost(esHostUrl, esPort)
                    ).setHttpClientConfigCallback(new RestClientBuilder.HttpClientConfigCallback() {
                        public HttpAsyncClientBuilder customizeHttpClient(HttpAsyncClientBuilder httpClientBuilder) {
                            httpClientBuilder.disableAuthCaching();
                            return httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                        }
                    })/*.setMaxRetryTimeoutMillis(2000)*/
            );
            String esSearchBasePath = "";
            if(esBasePath==null){
                esSearchBasePath="iccm/quality_record/";
            }else{
                esSearchBasePath = esBasePath;
            }
            if(esSearchBasePath.contains("/")){
                es_index = esSearchBasePath.substring(0,esSearchBasePath.indexOf("/"));
                String temp_type = esSearchBasePath.substring(esSearchBasePath.indexOf("/"));
                if(temp_type!=null){
                    String[] typeCells = temp_type.split("/");
                    StringBuilder sb = new StringBuilder();
                    for (String typeCell : typeCells) {
                        if(!"".equals(typeCell))
                            sb.append(typeCell).append("/");
                    }
                    es_type = sb.toString().substring(0,sb.lastIndexOf("/"));
                }
            }else{
                es_index = esSearchBasePath;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        if(transFile1==null||transFile2==null){
            logger.error("操作失败,没有指定转写结果文件");
            System.err.println("操作失败,没有指定转写结果文件");
            return "error";
        }
        //获取转写结果数据对象
        TransData transData1 = DataUtil.getTransDataFromFile(transFile1);
        TransData transData2 = DataUtil.getTransDataFromFile(transFile2);
        TransData transData = DataUtil.mergeTransData(transData1,transData2);
        List<Sentence> sentenceList = transData.getSentenceList();
        //写入ES
        /*组装保存到ES中的信息*/
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ESUtil esUtil = ESUtil.getInstance(esHostUrl);
        JSONObject jsonObj = new JSONObject();//用来封装保存到ES服务中的对象
        jsonObj.put("duration", transData1.getDuration()*1000);
        jsonObj.put("direction",transData.getDir());
        jsonObj.put("orig_reference", transData.getCallReference());
        jsonObj.put("media_from", transData.getCallerId());
        jsonObj.put("media_to", transData.getCalledId());
        jsonObj.put("start_time", sdf.format(transData.getStartTime()));
        jsonObj.put("extension", transData.getExtension());
        jsonObj.put("media_type", "00");
        jsonObj.put("media_platform", "001");
        JSONArray fragmentsArr = new JSONArray();
        // 读取转写的文字信息
        StringBuilder agentTxtSB = new StringBuilder();
        StringBuilder customerTxtSB = new StringBuilder();
        if(sentenceList!=null){
            for (Sentence sentence : sentenceList) {
                if("AGENT".equals(sentence.getRole())){//坐席说的话
                    agentTxtSB.append(sentence.getText()+" ");
                }else{//用户说的
                    customerTxtSB.append(sentence.getText()+" ");
                }
                JSONObject cellJson = new JSONObject();
                cellJson.put("from",sentence.getRole());
                cellJson.put("begin",sentence.getBegin());
                cellJson.put("end",sentence.getEnd());
                cellJson.put("text",sentence.getText());
                fragmentsArr.add(cellJson);
            }
        }
        jsonObj.put("agent_text",agentTxtSB.toString());
        jsonObj.put("customer_text",customerTxtSB.toString());
        jsonObj.put("fragments",fragmentsArr);
        StringBuilder referenceSB= new StringBuilder();
        referenceSB.append("Q").append(jsonObj.getString("media_type"))
                .append(jsonObj.getString("media_platform"))
                .append(transData.getCallReference());

        String path=es_index+"/"+es_type+"/"+referenceSB.toString();
        System.out.println("---提交路径:"+path);
        logger.info("---提交路径:"+path);
        logger.info(jsonObj.toJSONString());

        IndexRequest request = new IndexRequest(es_index, es_type, referenceSB.toString());
        request.source(jsonObj, XContentType.JSON);
        IndexResponse response = esClient.index(request);
        int statusCode = response.status().getStatus();
//        boolean flag = esUtil.createOrUpdateData(basicConfig.esSearchBasePath+referenceSB.toString(),jsonObj);
        logger.info("提交ES,返回的状态码:{},结果:{}",statusCode,response.getResult().getLowercase());
        if(statusCode==200||statusCode==201){
            logger.info("------"+transData.getCallReference()+":提交ES完成--------");
            return "success";
        }else{
            logger.error("提交ES操作出错,错误结果:"+response.getResult().getLowercase());
            logger.error("提交ES失败,失败录音:"+transData.getCallReference());
            return "error";
        }
    }
}
