package com.example.demo.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by raymond on 2016/1/7.
 */
public class XmlUtil {
    public Element rootElement;
    public Document xmlDoc;
    protected static final Logger logger = LoggerFactory.getLogger(XmlUtil.class);

    /**
     * 读取文件，初始化
     *
     * @throws DocumentException
     */
    public void readFile(String filePath) throws DocumentException {
        SAXReader sax = new SAXReader();
        xmlDoc = sax.read(new File(filePath));
        //根节点
        rootElement = xmlDoc.getRootElement();
    }

    /**
     * 修改文件
     *
     * @param filePath
     * @throws DocumentException
     * @throws IOException
     */

    public void commitUpate(String filePath) throws DocumentException, IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        format.setEncoding("UTF-8");
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileOutputStream(filePath) {
            }, format);
            //输出到文件
            writer.write(xmlDoc);
        } catch (IOException e) {
            System.err.println(String.format("write %s error", filePath));
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public List<Element> getElements(String xPath) {
        List list = rootElement.selectNodes(xPath);
        return list;
    }

    public void updateText(String xPath, String value) throws Exception {
        Element element = (Element) rootElement.selectObject(xPath);
        element.setText(value);
    }

    public String getText(String xPath) throws Exception {
        Element element = (Element) rootElement.selectObject(xPath);
        return element.getTextTrim();
    }

    /*得到attribute*/
    public String getAttribute(String xPath, String key) {
        String result = null;
        try {
            List list = rootElement.selectNodes(xPath);
            Element element = (Element) list.get(0);
            result = element.attributeValue(key);
        } catch (Exception e) {
            e.getStackTrace();
            if (key.equals("disabled")) {
                System.out.println("no delete node");
                result = "1";
            }
        }
        return result;
    }

    /*刷新指定xPath的attribute*/
    public void updateAttribute(String xPath, Map<String, String> attributesMap) throws Exception {
        Element element = (Element) rootElement.selectObject(xPath);
        for (String key : attributesMap.keySet()) {
            element.addAttribute(key, attributesMap.get(key));
        }
    }

    //重复节点
    public List<Element> getElements(String xPath, String name) {
        Element element = (Element) rootElement.selectObject(xPath);
        List<Element> l = element.elements(name);
        return l;
    }

    //获取节点
    public Element getNode(String xPath) {
        Element element = null;
        try {
            element = (Element) rootElement.selectObject(xPath);
        } catch (ClassCastException e) {
//            e.printStackTrace();
        }
        return element;
    }

    //添加节点
    public void addElement(String xPath, String eleName) {
        Element element = (Element) rootElement.selectObject(xPath);
        element.addElement(eleName);
    }

    //添加节点,同时添加属性
    public void addElementAttribute(String xPath, String eleName, Map<String, String> map) {
        Element element = (Element) rootElement.selectObject(xPath);
        Element subElement = element.addElement(eleName);
        for (String key : map.keySet()) {
            subElement.addAttribute(key, map.get(key));
        }
    }

    //添加节点,同时添加属性和text
    public void addElementAttributeText(String xPath, String eleName, Map<String, String> map, String text) {
        Element element = (Element) rootElement.selectObject(xPath);
        Element subElement = element.addElement(eleName);
        for (String key : map.keySet()) {
            subElement.addAttribute(key, map.get(key));
        }
        subElement.addText(text);
    }

    //删除指定节点下所有子节点
    public void deleteElements(String xPath) {
        Element element = (Element) rootElement.selectObject(xPath);
        element.getParent().remove(element);
    }


    /**
     * xml 格式字符串转成指定xml文件
     *
     * @param sourceContent
     * @param desFile
     * @throws DocumentException
     * @throws IOException
     */
    public static void strToXmlFile(String sourceContent, File desFile) throws DocumentException, IOException {
        Document document = DocumentHelper.parseText(sourceContent);
        strToXmlFile(document, desFile);
    }

    /**
     * dom4j document 转成指定xml 文件
     *
     * @param document
     * @param desFile
     * @throws DocumentException
     * @throws IOException
     */
    public static void strToXmlFile(Document document, File desFile) throws DocumentException, IOException {
        OutputFormat format = OutputFormat.createPrettyPrint();
        if (!desFile.exists()) {
            if (!desFile.createNewFile()) {
                throw new RuntimeException();
            }
        }
        format.setEncoding("UTF-8");
        XMLWriter writer = null;
        try {
            writer = new XMLWriter(new FileOutputStream(desFile) {
            }, format);
            //输出到文件
            writer.write(document);
        } catch (IOException e) {
            System.err.println(String.format("write %s error", desFile.getName()));
            System.err.println(e.getMessage());
            e.printStackTrace();
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    public static boolean validXmlFile(File xmlFile) {
        SAXReader sax = new SAXReader();
        try {
            sax.read(xmlFile);
        } catch (DocumentException e) {
            return false;
        }
        return true;
    }

    public void createXml(String rootElementName) {
        xmlDoc = DocumentHelper.createDocument();
        xmlDoc.addElement(rootElementName);
        rootElement = xmlDoc.getRootElement();
    }
}
