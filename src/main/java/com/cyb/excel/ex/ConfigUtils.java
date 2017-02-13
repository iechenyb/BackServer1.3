package com.cyb.excel.ex;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

public class ConfigUtils {
	private static Properties p = null;
	public synchronized static void init() throws Exception {
		InputStream inputstream = null;
		try {
			if (p == null) 
			{
				p = new Properties();
				String filePath = System.getProperty("user.dir")+File.separator+"jx/App.properties";
				System.out.println("配置文件路径 "+filePath);
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

public static String get(String key) {
		String result = "";
		try {
			result = p.getProperty(key);
			return result;
		} catch (Exception e) {
			e.printStackTrace();
			return "";
		}
	}

	public static void main(String[] s) throws Exception {
		 ConfigUtils.init();
		 System.out.println(ConfigUtils.get("lastMonthOfPreYear"));
	}
}