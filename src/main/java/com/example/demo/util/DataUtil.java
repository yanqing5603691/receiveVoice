package com.example.demo.util;

import com.alibaba.fastjson.JSONObject;

import com.example.demo.model.Sentence;
import com.example.demo.model.TransData;
import org.dom4j.DocumentException;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @Auther: fujian
 * @Date: 2018/12/20 10:56
 * @Description: 获取数据的工具类
 */
public class DataUtil {
    private static Logger logger = LoggerFactory.getLogger(DataUtil.class);

    public static void main(String[] args) throws Exception{
       /*String url = "jdbc:sqlserver://192.168.4.117:1433;DatabaseName=vclogdbld;";
       Connection conn= DriverManager.getConnection(url,"sa","voicecodes");
       String sql = "select count(0) as count from recordOriginalData";
        ResultSet rs = getSqlResult(conn,sql,null);
        while (rs.next()){
            int count = rs.getInt("count");
            System.out.println(count);
        }*/
       //getSentenceFromTransFile("E:\\transFile\\11111.trans");
    }

    public static JSONObject getFromJsonFile(String jsonFilePath){
        String context = readFile(jsonFilePath);
        if(context!=null){
            JSONObject json = JSONObject.parseObject(context);
            if(json==null){
                json = new JSONObject();
            }
            return json;
        }
        return new JSONObject();
    }

    public static String readFile(String Path){
        BufferedReader reader = null;
        String laststr = "";
        try{
            FileInputStream fileInputStream = new FileInputStream(Path);
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream, "UTF-8");
            reader = new BufferedReader(inputStreamReader);
            String tempString = null;
            while((tempString = reader.readLine()) != null){
                laststr += tempString;
            }
            reader.close();
        }catch(IOException e){
            e.printStackTrace();
        }finally{
            if(reader != null){
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return laststr;
    }

    public static PreparedStatement getPs(Connection conn, String sql,List<String> paramList){
        PreparedStatement stmt=null;
        int paramCount = sql.split("\\?").length-1;//sql中需要的参数个数
        try {
            stmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
            if(paramList!=null&&paramList.size()>0){
                for(int i=1;i<=paramCount;i++){
                    if(i<=paramList.size()){
                        stmt.setString(i,paramList.get(i-1));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stmt;
    }

    /**
     * 该方法存在问题,没有关闭sql查询生成的ps对象,因为在方法内关闭的话,外部无法使用resultSet对象
     * @param conn
     * @param sql
     * @param paramList
     * @return
     */
    @Deprecated
    public static ResultSet getSqlResult(Connection conn, String sql,List<String> paramList){
        PreparedStatement stmt=null;
        ResultSet rs = null;
        int paramCount = sql.split("\\?").length-1;//sql中需要的参数个数
        try {
             stmt = conn.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_READ_ONLY);
             if(paramList!=null&&paramList.size()>0){
                 for(int i=1;i<=paramCount;i++){
                     if(i<=paramList.size()){
                         stmt.setString(i,paramList.get(i-1));
                     }
                 }
             }
             rs = stmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }/*finally {
            if(stmt!=null) {
                try {
                    stmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }*/
        return rs;
    }

    public static JSONObject getXmlJson(String xmlPath)throws Exception {
        JSONObject object = new JSONObject();
        SAXBuilder builder = null;
        try {
            builder = new SAXBuilder();
            Document document = builder.build(new File(xmlPath));
            Element element =document.getRootElement();
            object.put(element.getName(),IterateElement(element));
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return  null;
        }
    }

    private static Map IterateElement(Element element){
        List node = element.getChildren();
        List list = null;
        Map map = new HashMap();
        for(int i=0;i<node.size();i++){
            list = new LinkedList();
            Element element1 = (Element) node.get(i);
            if(element1.getTextTrim().equals("")){
                if(element1.getChildren().size()==0){
                    continue;
                }
                if(map.containsKey(element1.getName())){
                    list = (List) map.get(element1.getName());
                }
                list.add(IterateElement(element1));
                map.put(element1.getName(),list);
            }else{
                if(map.containsKey(element1.getName())){
                    list = (List) map.get(element1.getName());
                }
                list.add(element1.getTextTrim());
                map.put(element1.getName(),list);
            }
        }
        return  map;
    }

    /**
     * 从properties中获取指定类型的value
     * @param pro
     * @param key
     * @param valClass
     * @param <T>
     * @return
     */
    public static <T>T getValue(Properties pro,String key,Class valClass){
        String value = pro.getProperty(key);
        if(valClass == Integer.class){
            return (T)new Integer(value);
        }else if(valClass ==  String.class){
            return (T)value;
        }else if(valClass==Long.class){
            return (T) new Long(value);
        }
        return null;
    }

    public static <T>T getValue(Properties pro,String key,Class valClass,T defaultVal){
        String value = pro.getProperty(key);
        if(value==null){
            return defaultVal;
        }
        if(valClass == Integer.class){
            return (T)new Integer(value);
        }else if(valClass ==  String.class){
            return (T)value;
        }else if(valClass==Long.class){
            return (T) new Long(value);
        }else if(valClass==Boolean.class){
            return (T) new Boolean(value);
        }
        return defaultVal;
    }

    /**
     * 从捷通转写结果文件中获取结果信息
     * @param transFilePath
     * @return
     */
//    public static List<Sentence> getSentenceFromTransFile(String transFilePath){
//        XmlUtil xmlUtil = new XmlUtil();
//        try {
//            xmlUtil.readFile(transFilePath);
//        } catch (DocumentException e) {
//            System.err.println("加载文件-"+transFilePath+"-出现异常:"+e.getMessage());
////            e.printStackTrace();
//            return null;
//        }
//        List<org.dom4j.Element> sentenceEles =  xmlUtil.getElements("//result/sentence_list/sentence");
//        List<Sentence> sentenceList = new ArrayList<>();
//        for (org.dom4j.Element sentenceEle : sentenceEles) {
//            String role = sentenceEle.attributeValue("role");
//            String text = sentenceEle.selectSingleNode("text").getText();
//            String start = sentenceEle.attributeValue("start");
//            String end = sentenceEle.attributeValue("end");
//            Sentence sentence = new Sentence();
//            sentence.setRole(role);
//            sentence.setText(text);
//            sentence.setBegin(Long.parseLong(start));
//            sentence.setEnd(Long.parseLong(end));
//            sentenceList.add(sentence);
//        }
//
//        return sentenceList;
//    }

    /**
     * 解析统一格式的转写结果文件，封装成转写结果数据对象
     * @param transFile
     * @return
     * @throws Exception
     */
    public static TransData getTransDataFromFile(String transFile)throws Exception{
        XmlUtil xmlUtil = new XmlUtil();
        if(transFile==null){
            logger.warn("封装转写结果失败,传入的转写文件不能为空");
            return null;
        }
        try {
            xmlUtil.readFile(transFile);
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        TransData transData = new TransData();
        String role = xmlUtil.getText("//analyze/callInfo/role");
        switch (role){
            case "AGENT":
                transData.setDataType(TransData.DataType.AGENTDATA);
                break;
            case "USER":
                transData.setDataType(TransData.DataType.USERDATA);
                break;
            default:
                transData.setDataType(TransData.DataType.OTHER);
        }
        String extension = xmlUtil.getText("//analyze/callInfo/extension");
        transData.setExtension(extension);
        String callerId = xmlUtil.getText("//analyze/callInfo/callerId");
        transData.setCallerId(callerId);
        String calledId = xmlUtil.getText("//analyze/callInfo/calledId");
        transData.setCalledId(calledId);
        String dirStr = xmlUtil.getText("//analyze/callInfo/dir");
        transData.setDir(Integer.parseInt(dirStr));
        String startTimeStr = xmlUtil.getText("//analyze/callInfo/startTime");
        transData.setStartTime((sdf.parse(startTimeStr)));
        String durationStr = xmlUtil.getText("//analyze/callInfo/duration");
        transData.setDuration(Long.parseLong(durationStr));
        String callReference = xmlUtil.getText("//analyze/callInfo/callReference");
        transData.setCallReference(callReference);
        List<Sentence> sentenceList = getSentencesFromTransFile(transFile);
        transData.setSentenceList(sentenceList);
        return transData;
    }

    /**
     * 这是新的统一格式转写结果文件的解析
     * @param transFile
     * @return
     */
    public static List<Sentence> getSentencesFromTransFile(String transFile)throws Exception{
        XmlUtil xmlUtil = new XmlUtil();
        try {
            xmlUtil.readFile(transFile);
        } catch (DocumentException e) {
            System.err.println("加载文件-"+transFile+"-出现异常");
            e.printStackTrace();
            return null;
        }
        String role = xmlUtil.getText("//analyze/callInfo/role");
        List<org.dom4j.Element> sentenceEles =  xmlUtil.getElements("//analyze/subject/function");
        List<Sentence> sentenceList = new ArrayList<>();
        for (org.dom4j.Element sentenceEle : sentenceEles) {
            String text = sentenceEle.selectSingleNode("text").getText();
            String time = sentenceEle.selectSingleNode("time").getText();
            String []timeDur = time.split(",");
            String start = timeDur[0];
            String end = timeDur[1];
            Sentence sentence = new Sentence();
            sentence.setRole(role);
            sentence.setText(text);
            sentence.setBegin(Long.parseLong(start));
            sentence.setEnd(Long.parseLong(end));
            sentenceList.add(sentence);
        }
        return sentenceList;
    }

    /**
     * 合并转写结果数据
     * @param transData1
     * @param transData2
     * @return
     */
    public static TransData mergeTransData(TransData transData1,TransData transData2){
        TransData transData = null;
        if(transData1.getCallReference().equals(transData2.getCallReference())){
            List<Sentence> transData1SentenceList = transData1.getSentenceList();
            List<Sentence> transData2SentenceList = transData2.getSentenceList();
            List<Sentence> sentenceList = new ArrayList<>();
            sentenceList.addAll(transData1SentenceList);
            sentenceList.addAll(transData2SentenceList);
            Collections.sort(sentenceList);//排序
            if(transData1.getDataType()==TransData.DataType.AGENTDATA){
                transData = transData1;
            }else if(transData2.getDataType()==TransData.DataType.AGENTDATA){
                transData = transData2;
            }else{
                logger.error("合并失败，传入的转写结果没有属于AGENT角色的");
                throw new IllegalArgumentException("合并失败，传入的转写结果没有属于AGENT角色的");
            }
            transData.setSentenceList(sentenceList);
            transData.setDataType(TransData.DataType.MERGEDATA);
        }else{
            logger.error("合并失败，两个转写结果不属于同一次通话记录。");
            throw new IllegalArgumentException("合并失败，两个转写结果不属于同一次通话记录。");
        }
        return transData;
    }

    public static String getBlankIfNull(String value){
        return value==null?"":value;
    }
}
