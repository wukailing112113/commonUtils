package com.wins.shop.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.ConnectionPoolTimeoutException;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.log4j.Logger;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import com.thoughtworks.xstream.io.xml.XmlFriendlyNameCoder;

public class HttpClient {
	
	public static final Logger logger = Logger.getLogger(HttpClient.class);
	public String post(String targetUrl, String params, String encode) throws Exception{
		InputStream httpIn = null;
		InputStreamReader inr = null;
		BufferedReader br = null;
		OutputStream out = null;
		HttpURLConnection httpconn = null;
		try{
			URL url = new URL(targetUrl);

			URLConnection connection = url.openConnection();
			httpconn = (HttpURLConnection) connection;
			
			httpconn.setRequestMethod("POST");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(true);
			httpconn.setConnectTimeout(3000);
			
			out = httpconn.getOutputStream();
			out.write(params.getBytes(encode));
			//out.close();

			httpIn = httpconn.getInputStream();
			inr = new InputStreamReader(httpIn, encode);
			br = new BufferedReader(inr);
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while (null != (inputLine = br.readLine())) {
				sb.append(inputLine);
			}
			return sb.toString();
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("httpclient post失败！" + e.getMessage());
		}finally{
			
			if ( null != br){
				br.close();
				br = null;
			}
			
			if ( null != inr){
				inr.close();
				inr = null;
			}
			
			if ( null != httpIn){
				httpIn.close();
				httpIn = null;
			}
			
			if ( null != out){
				out.close();
				out = null;
			}
			
			if ( null != httpconn){
				httpconn.disconnect();
				httpconn = null;
			}
		}
		
	}
	
	public String get(String targetUrl, String encode) throws Exception{
		InputStream httpIn = null;
		InputStreamReader inr = null;
		BufferedReader br = null;
		HttpURLConnection httpconn = null;
		try{
			URL url = new URL(targetUrl);

			URLConnection connection = url.openConnection();
			httpconn = (HttpURLConnection) connection;
			
			httpconn.setRequestMethod("GET");
			httpconn.setDoInput(true);
			httpconn.setDoOutput(false);
			httpconn.setConnectTimeout(3000);
			httpIn  = httpconn.getInputStream();
			inr = new InputStreamReader(httpconn.getInputStream());
			br = new BufferedReader(inr);
			String inputLine;
			StringBuilder sb = new StringBuilder();
			while (null != (inputLine = br.readLine())) {
				sb.append(new String(inputLine.getBytes(), encode));
			}
			return sb.toString();
		}catch (Exception e){
			e.printStackTrace();
			throw new Exception("httpclient get请求失败" + e.getMessage());
		}finally{
			
			if ( null != br){
				br.close();
				br = null;
			}
			
			if ( null != inr){
				inr.close();
				inr = null;
			}
			
			if ( null != httpIn){
				httpIn.close();
				httpIn = null;
			}
			
			if ( null != httpconn){
				httpconn.disconnect();
				httpconn = null;
			}
			
		}
		
	}
	
	public String postXml(String targetUrl,String encode,Object params) throws IOException{
		 String result = null;

	        HttpPost httpPost = new HttpPost(targetUrl);

	        
	        //解决XStream对出现双下划线的bug
	        XStream xStreamForRequestPostData = new XStream(new DomDriver(encode, new XmlFriendlyNameCoder("-_", "_")));

	        //将要提交给API的数据对象转换成XML格式数据Post给API
	        String postDataXML = xStreamForRequestPostData.toXML(params);

	        logger.debug("API，POST过去的数据是：");
	        logger.debug(postDataXML);

	        //得指明使用UTF-8编码，否则到API服务器XML的中文不能被成功识别
	        StringEntity postEntity = new StringEntity(postDataXML, encode);
	        httpPost.addHeader("Content-Type", "text/xml");
	        httpPost.setEntity(postEntity);

	        //设置请求器的配置
	        //httpPost.setConfig(requestConfig);

	        logger.debug("executing request" + httpPost.getRequestLine());
	        CloseableHttpClient httpClient = null;
	        try {
	        	httpClient = HttpClients.createDefault();
	        	
	            HttpResponse response = httpClient.execute(httpPost);

	            HttpEntity entity = response.getEntity();

	            result = EntityUtils.toString(entity, encode);

	        } catch (ConnectionPoolTimeoutException e) {
	        	logger.debug("http get throw ConnectionPoolTimeoutException(wait time out)");

	        } catch (ConnectTimeoutException e) {
	        	logger.debug("http get throw ConnectTimeoutException");

	        } catch (SocketTimeoutException e) {
	        	logger.debug("http get throw SocketTimeoutException");

	        } catch (Exception e) {
	        	logger.debug("http get throw Exception");

	        } finally {
	        	
	        	if (null != httpClient){
	        		httpClient.close();
	        		httpClient = null;
	        	}
	        	
	        	if ( null != httpPost){
	        		httpPost.abort();
	        		httpPost = null;
	        	}
	            
	        }

	        return result;
	}
}
