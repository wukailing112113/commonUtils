package com.wins.shop.util;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.xml.sax.InputSource;

public class XMLUtil {
	
	private static final Logger logger = Logger.getLogger(XMLUtil.class);
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMapFromXML(String xmlString) throws Exception {
    	Map<String, String> map = new HashMap<String, String>();
    	StringReader read = null;
    	try{
    		read = new StringReader(xmlString);
    		// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
    		InputSource source = new InputSource(read);
    		// 创建一个新的SAXBuilder
    		SAXBuilder sb = new SAXBuilder();
    		// 通过输入源构造一个Document
    		Document doc = (Document) sb.build(source);
    		Element root = doc.getRootElement();// 指向根节点
    		List<Element> es = root.getChildren();
    		if (es != null && es.size() != 0) {
    			for (Element element : es) {
    				map.put(element.getName(), element.getValue());
    			}
    		}
    	}catch (Exception ex){
    		ex.printStackTrace();
    		throw new Exception("解析xml失败！" + ex.getMessage());
    		
    	}finally{
    		if ( null != read){
    			read.close();
    			read = null;
    		}
    	}
    	
    	return map;

    }
	
	@SuppressWarnings("unchecked")
	public static Map<String,String> getMapFromInputStreamXML(InputStream in,String encoding) throws Exception {
		Map<String, String> map = new HashMap<String, String>();
    	//BufferedReader br = new BufferedReader(new InputStreamReader(in, encoding));
		
		InputStreamReader inr = null;
		BufferedReader br = null;
		try{
			inr = new InputStreamReader(in, encoding);
			br = new BufferedReader(inr);
			// 创建新的输入源SAX 解析器将使用 InputSource 对象来确定如何读取 XML 输入
			InputSource source = new InputSource(br);
			// 创建一个新的SAXBuilder
			SAXBuilder sb = new SAXBuilder();
			// 通过输入源构造一个Document
			Document doc = (Document) sb.build(source);
			Element root = doc.getRootElement();// 指向根节点
			List<Element> es = root.getChildren();
			if (es != null && es.size() != 0) {
				for (Element element : es) {
					map.put(element.getName(), element.getValue());
					logger.debug("name=" + element.getName() + ",value=" + element.getValue());
				}
			}
    	}catch (Exception ex){
    		ex.printStackTrace();
    		throw new Exception("解析xml失败！" + ex.getMessage());
    	}finally{
    		
    		if ( null != br){
    			br.close();
    			br = null;
    		}
    		if ( null != inr){
    			inr.close();
    			inr = null;
    		}
    		
//    		if ( null != in){
//    			in.close();
//    			in = null;
//    		}
    	}
    	return map;

    }
}
