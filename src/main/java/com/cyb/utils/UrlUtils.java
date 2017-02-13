package com.cyb.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.SecureRandom;
import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.X509Certificate;

import net.sf.json.JSONArray;

public class UrlUtils {
	private static final String METHOD_POST = "POST";  
    private static final String DEFAULT_CHARSET = "utf-8";  
    //https://222.66.97.215/public/gnAccountLogin.do?userName=00050&password=MTIzNDU2&alias=12323
    //https://222.66.97.215/public/checkIfLogin.do?gnAccount=00050&token=0aaf33d09a2d456bebe3e78b57a433bc
    private static String url = "https://222.66.97.215/public/checkIfLogin.do?gnAccount=000045&token=123456";
//    {"ifLogin":false} 
    //    private static String url ="https://222.66.97.215/public/appLogin.do?userName=000045&password=111&alias=123456";    
//    {"resultStatus":"-912","resultMessage":"用户名(字母、数字、汉字组合)、密码(字母、数字组合)以及手机号码(11位数字)格式不正确"}
    public static  void test(){
		HttpsURLConnection conn = null;  
	    OutputStream out = null;  
	    try {  
	        try{  
	            SSLContext ctx = SSLContext.getInstance("TLS");  
	            ctx.init(new KeyManager[0], new TrustManager[] {new DefaultTrustManager()}, new SecureRandom());  
	            SSLContext.setDefault(ctx);  
	            conn = getConnection(new URL(url), METHOD_POST, "");   
	            conn.setHostnameVerifier(new HostnameVerifier() {  
	                public boolean verify(String hostname, SSLSession session) {  
	                    return true;  
	                }  
	            });  
	            conn.setConnectTimeout(1000);  
	            conn.setReadTimeout(1000);  
	        }catch(Exception e){  
	            System.out.println("GET_CONNECTOIN_ERROR, URL = "+ e);  
	        }  
	        try{  
	        	BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
	        	String content = "";
	        	content = reader.readLine();
	        	JSONArray data = JSONArray.fromObject("["+content+"]");
	        	Boolean canLogin = (Boolean) data.getJSONObject(0).get("ifLogin");
	        	System.out.println(content+":"+canLogin);
	        }catch(IOException e){  
	            System.out.println("REQUEST_RESPONSE_ERROR");
	        }  
	    }finally {  
	        if (out != null) {  
	            try {
					out.close();
				} catch (IOException e) {
					System.out.println("CLOSE CONNECTION FAILED!");
				}  
	        }  
	        if (conn != null) {  
	            conn.disconnect();  
	        }  
	    }  
	}
	public static  HttpsURLConnection getConnection(URL url,String method,String ctype){  
	    HttpsURLConnection conn = null;
		try {
			conn = (HttpsURLConnection) url.openConnection();  
			conn.setRequestMethod(method);  
			conn.setDoInput(true);  
			conn.setDoOutput(true);  
			conn.setRequestProperty("Accept", "text/xml,text/javascript,text/html");  
			conn.setRequestProperty("User-Agent", "stargate");  
			conn.setRequestProperty("Content-Type", ctype);
		} catch (ProtocolException e) {
			System.out.println("ProtocolException "+e.toString());
		} catch (IOException e) {
			System.out.println("IOException "+e.toString());
		}  
	    return conn;  
	}  
	public static InputStream getStream(String address){
		InputStream is = null;
		try {
			URL url = new URL(address);
			HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
			is = urlcon.getInputStream();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is;
	}
	public static void downLoadContentByStream(String address,String toDir){
		 try{
	           /*
	            *   方法一
	            *     
	           URL url = new URL("http://www.sina.com.cn");
	           URLConnection urlcon = url.openConnection();
	           InputStream is = urlcon.getInputStream();
	            */
	          
	           /*
	            * 方法二
	            *
	            * URL url = new URL("http://www.yhfund.com.cn");
	           HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
	           InputStream is = urlcon.getInputStream();
	            */
	          
	          
	          
	           /*
	            * 方法三
	            * URL url = new URL("http://www.yhfund.com.cn");
	              InputStream is = url.openStream();
	            */
	           long begintime = System.currentTimeMillis();
	           URL url = new URL(address);
	           HttpURLConnection urlcon = (HttpURLConnection)url.openConnection();
	           urlcon.connect();         //获取连接
	           InputStream is = urlcon.getInputStream();
	           BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
	           StringBuffer bs = new StringBuffer();
	           String l = null;
	           while((l=buffer.readLine())!=null){
	               bs.append(l).append("/n");
	           }
	           System.out.println(bs.toString());
	          
	           //System.out.println(" content-encode："+urlcon.getContentEncoding());
	           //System.out.println(" content-length："+urlcon.getContentLength());
	           //System.out.println(" content-type："+urlcon.getContentType());
	           //System.out.println(" date："+urlcon.getDate());
	      
	          
	           System.out.println("总共执行时间为："+(System.currentTimeMillis()-begintime)+"毫秒");
	        }catch(IOException e){
	           System.out.println(e);
	       }
	}
	public static void main(String[] args) {
		test();
	}

}


class DefaultTrustManager implements X509TrustManager {  
	public void checkClientTrusted(java.security.cert.X509Certificate[] chain,
			String authType) throws CertificateException {
	}
	public void checkServerTrusted(java.security.cert.X509Certificate[] chain,
			String authType) throws CertificateException {
	}
	public java.security.cert.X509Certificate[] getAcceptedIssuers() {
		return null;
	}
}
