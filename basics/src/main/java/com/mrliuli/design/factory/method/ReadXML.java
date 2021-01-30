package com.mrliuli.design.factory.method;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 * @author liu.li
 * @date 2021/1/30
 * @description
 */
public class ReadXML {

    public static Object getObject() {
        try {

            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
            Document document = documentBuilder.parse(new File("D:\\my\\java-interview\\basics\\src\\main\\java\\com\\mrliuli\\design\\factory\\method\\config.xml"));
            NodeList nodeList = document.getElementsByTagName("className");
            Node classNode = nodeList.item(0).getFirstChild();
            String className = classNode.getNodeValue();
            Class<?> clazz = Class.forName("com.mrliuli.design.factory.method." + className);
            Object o = clazz.newInstance();
            return o;

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
