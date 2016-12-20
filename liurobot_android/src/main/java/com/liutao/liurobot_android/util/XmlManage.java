package com.liutao.liurobot_android.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.os.Environment;

public class XmlManage {

	private static String filename = "config.xml";
	private static File xmlPath = new File(
			Environment.getExternalStorageDirectory(), filename);
	

	public static void getNodeList(String tagnames,String tagname) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(xmlPath); // 使用dom解析xml文件
			
			NodeList nodelist = doc.getElementsByTagName(tagnames);
			for (int i = 0; i < nodelist.getLength(); i++) // 循环处理对象
			{
				Element root = (Element) nodelist.item(i);
				String attr = root.getAttribute("name");
				System.out.println("该节点属性为"+attr);
				
				NodeList sonlist = root.getElementsByTagName(tagname);
				for (int j = 0; j < sonlist.getLength(); j++) {
					String sonvalue = sonlist.item(j).getFirstChild().getNodeValue();
					String sonname = sonlist.item(j).getNodeName();
					System.out.println(sonname+" : "+sonvalue);
					
						
				}

			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void modifySon() {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmldoc = db.parse(xmlPath);

			Element root = xmldoc.getDocumentElement();

			Element per = (Element) selectSingleNode("/father/son[@id='001']",
					root);
			per.getElementsByTagName("age").item(0).setTextContent("media-768");

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer former = factory.newTransformer();
			former.transform(new DOMSource(xmldoc), new StreamResult(new File(
					xmlPath.toString())));
		} catch (Exception e) {
			
		}
	}

	public static void discardSon() {

		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(true);

		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmldoc = db.parse(xmlPath);

			Element root = xmldoc.getDocumentElement();

			Element son = (Element) selectSingleNode("/father/son[@id='002']",
					root);
			root.removeChild(son);

			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer former = factory.newTransformer();
			former.transform(new DOMSource(xmldoc), new StreamResult(new File(
					xmlPath.toString())));

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void createSon(String libraryname,String roots,String attrname,String nodename,String nodevalue) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setIgnoringElementContentWhitespace(false);
		
		try {

			DocumentBuilder db = dbf.newDocumentBuilder();
			Document xmldoc = db.parse(xmlPath);

			NodeList nodelist = xmldoc.getElementsByTagName(libraryname);
			Element library = (Element) nodelist.item(0);
			
			Element rootnode = xmldoc.createElement(roots);
			rootnode.setAttribute("action", attrname);
			
			Element sonnode = xmldoc.createElement(nodename);
			sonnode.setTextContent(nodevalue);
			rootnode.appendChild(sonnode);
			  
			// Here need to modify
			  
			// 保存
			  
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer former = factory.newTransformer();
			former.transform(new DOMSource(xmldoc), new StreamResult(new File(
					xmlPath.toString())));
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static Node selectSingleNode(String express, Element source) {
		Node result = null;
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		try {
			result = (Node) xpath
					.evaluate(express, source, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
		 	e.printStackTrace();
		}

		return result;
	}

	public static void main(String[] args) {
		

	
	
	
		
		
		
		
	}
}
