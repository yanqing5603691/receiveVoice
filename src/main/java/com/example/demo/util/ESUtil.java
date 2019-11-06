package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStreamReader;
import java.nio.charset.Charset;

/**
 * @Auther: fujian
 * @Date: 2018/9/7 17:16
 * @Description: 操作ES 服务的工具类
 */
public class ESUtil {
    private Logger log = LoggerFactory.getLogger(ESUtil.class);
    private static String hostUrl;
    private static ESUtil esUtil;
    private static HttpClient httpClient;
    private ESUtil(){}
   /* public static ESUtil init(String hostUrl){
        if(esUtil==null){
            synchronized (ESUtil.class){
                if(esUtil==null){
                    esUtil = new ESUtil();
                    esUtil.hostUrl = hostUrl;
                }
            }
        }
        return esUtil;
    }*/

    public static ESUtil getInstance(String hostUrl)throws Exception{
        httpClient = HttpClients.createDefault();
        ESUtil.hostUrl = hostUrl;
        if(esUtil==null){
            synchronized (ESUtil.class){
                if(esUtil==null){
                    esUtil = new ESUtil();
                    esUtil.hostUrl = hostUrl;
                }
            }
        }
        return esUtil;
    }


    /**
     * 创建ES索引
     * @param indexName
     * @return
     */
    public boolean createIndex(String indexName){
        HttpPut put = new HttpPut(hostUrl+"/"+indexName+"?pretty");
        try {
            HttpResponse resp =  httpClient.execute(put);
            String result = getRespContent(resp);
            JSONObject json = JSONObject.parseObject(result);
            if(json.getBoolean("acknowledged")){
                log.info("创建ES索引成功，索引名："+indexName);
                return true;
            }else{
                JSONObject errorJson = json.getJSONObject("error");
                if(errorJson!=null){
                    String reason = errorJson.getString("reason");
                    log.error("创建ES索引失败："+reason);
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return false;
    }


    /**
     *定义向索引插入或更新数据的方法
     * @param path 指定数据保存的ES路径 如: produce/bike
     * @param jsonData 保存的数据
     * @return 保存成功返回true,否则false
     */
    public boolean createOrUpdateData(String path, JSONObject jsonData){
        HttpPost post = new HttpPost(hostUrl+"/"+path+"?pretty");
        //构件消息体
        StringEntity entity = new StringEntity(jsonData.toJSONString(), Charset.forName("UTF-8"));
        entity.setContentEncoding("UTF-8");
        //发送json格式的数据请求
        entity.setContentType("application/json");
        post.setEntity(entity);
       /* RequestConfig requestConfig = RequestConfig.custom().setSocketTimeout(2000).setConnectTimeout(2000).build();//设置请求和传输超时时间
        post.setConfig(requestConfig);*/
        try {
            HttpResponse resp = httpClient.execute(post);
            String result = getRespContent(resp);
            JSONObject json = JSONObject.parseObject(result);
            String exeResult = json.getString("result");
            if("created".equals(exeResult)||"updated".equals(exeResult)){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 定义删除索引的方法
     * @param indexName 索引名称
     * @return
     * @throws Exception
     */
    public boolean deleteIndex(String indexName){
        HttpDelete delete = new HttpDelete(hostUrl+"/"+indexName);
        try {
            HttpResponse resp = httpClient.execute(delete);
            String result = getRespContent(resp);
            JSONObject json = JSONObject.parseObject(result);
            if("deleted".equals(json.getString("result"))){
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 获取指定路径文档数据字符串
     * @param docPath 文档的ES路径
     * @return 直接返回ES服务器返回结果,需要自行判断返回结果是否正确返回(判断"found"字段是否为true)
     */
    public String getDocData(String docPath){
        HttpGet get = new HttpGet(hostUrl+"/"+docPath);
        try {
            HttpResponse resp = httpClient.execute(get);
            String result = getRespContent(resp);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    private String getRespContent(HttpResponse resp)throws Exception{
        int statusCode= statusCode = resp.getStatusLine().getStatusCode();
        String result = "";
        if(statusCode==200|statusCode==201){
            InputStreamReader reader = new InputStreamReader(resp.getEntity().getContent());
//            result = CharStreams.toString(new InputStreamReader(resp.getEntity().getContent(), Charsets.UTF_8)); ;
            result = IOUtils.toString(reader);
            System.out.println(result);
        }else{
            log.warn("请求失败 返回的状态码为:"+statusCode);
        }
        return result;
    }

}
