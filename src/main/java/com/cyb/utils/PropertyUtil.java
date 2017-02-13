package com.cyb.utils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PropertyUtil {

	private static Properties p = null;
	static Log log = LogFactory.getLog(PropertyUtil.class);
	public synchronized static void init(String propertyName) throws Exception {
		InputStream inputstream = null;
		try {
			if (p == null) 
			{
				p = new Properties();
				String filePath = Contants.WEBPATH + "WEB-INF"+File.separator +"classes"+ File.separator + propertyName + ".properties";
				log.info("初始化属性文件:"+filePath);
				inputstream = new FileInputStream(filePath);
				p.load(inputstream);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if(inputstream!=null){
				inputstream.close();
				inputstream = null;
			}
		}
	}
public static String getValueByKey(String propertyName, String key) {
		String result = "";
		try {
			if(p==null){
				init(propertyName);
			}
			result = p.getProperty(key);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}
public static String get(String key) {
	String result = "";
	try {
		if(p==null){
			init("App");
		}
		result = getValueByKey("App",key);
		return result;
	} catch (Exception e) {
		e.printStackTrace();
		return "";
	}
}
public static void resetValue(String key,String value)
{
	Properties pro=p;
	String filePath = Contants.WEBPATH + "WEB-INF"+File.separator +"classes"+ File.separator +"App.properties";
	log.info("初始化属性文件:"+filePath);
	try {
		InputStream in = new FileInputStream(filePath);
		pro.load(in);
		pro.setProperty(key,value);
		pro.remove("");
		OutputStream os = null;
		FileUtils.copyFileByStream(in, filePath);//重写到文件
		os = new FileOutputStream(filePath);
		pro.store(os, "modify by iechenyb");//重新加载内容
		os.flush();
		os.close();
		in.close();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
public static void main(String[] s) {
	 System.out.println(PropertyUtil.getValueByKey("App.properties","cfcenter"));
}
}