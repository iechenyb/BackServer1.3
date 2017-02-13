package com.cyb.qutoes.utils;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.htmlparser.Node;
import org.htmlparser.Parser;
import org.htmlparser.filters.RegexFilter;
import org.htmlparser.util.NodeList;
import org.htmlparser.util.ParserException;

import com.cyb.qutoes.dao.impl.GrabDataDaoImpl;
import com.cyb.utils.FileUtils;
import com.cyb.utils.SpringUtil;

public class GrabEntity {
	public static List<Map<String, Object>> stocks1 = null;
	public static List<Map<String, Object>> stocks2 = null;
	public static Log log = LogFactory.getLog(GrabEntity.class);
	public static String cfcenter = "http://quote.eastmoney.com/stocklist.html";
	public static String codeFilePath = "D:";
	public static String codeFileName = "code.html";
	public static String path1 = "d:/stock/shstocks.txt";
	public static String path1_1 ="d:/stock/shstocks_1.txt";
	public static String path2 = "d:/stock/szstocks.txt";
	public static String path2_1 ="d:/stock/szstocks_1.txt";
	/**
	 * 下载stock html文件
	 */
	public static void grabCodeFromCFCenter(){
		try {
			String savepath = codeFilePath+File.separator+codeFileName;
			File codeFile = new File(savepath);
			if(!codeFile.exists()){
				downLoadFromUrl(cfcenter,savepath);
			}
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(codeFile),"GBK"));
			String content = "";
			int count =0;
			while((content = reader.readLine())!=null){
				if(content.contains("<li><a")){
					//log.info(content);
					count++;
					log.info(getCodeInfor(content));
				}
			}
			log.info("一共"+count+"个节点！");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public static void resolveCodeFromHtmlFile(){
		try {
			String savepath=System.getProperty("user.dir")+"\\src\\main\\java\\com\\cyb\\qutoes\\utils\\"+codeFileName;
			File codeFile = new File(savepath);
			if(!codeFile.exists()){
				log.info("下载中...");
				downLoadFromUrl(cfcenter,savepath);
			}
			@SuppressWarnings("resource")
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(codeFile),"GBK"));
			String content = "";
			int count =0;
			while((content = reader.readLine())!=null){
				if(content.contains("<li><a")){
					//log.info(content);
					count++;
					//log.info(getCodeInfor(content));
				}
			}
			log.info("一共"+count+"个节点！");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	 public static void  downLoadFromUrl(String urlStr,String savePath) throws IOException{
	        URL url = new URL(urlStr);  
	        HttpURLConnection conn = (HttpURLConnection)url.openConnection();  
	                //设置超时间为3秒
	        conn.setConnectTimeout(3*1000);
	        //防止屏蔽程序抓取而返回403错误
	        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

	        //得到输入流
	        InputStream inputStream = conn.getInputStream();  
	        //获取自己数组
	        byte[] getData = readInputStream(inputStream);    

	        //文件保存位置
	        File file = new File(savePath);    
	        FileOutputStream fos = new FileOutputStream(file);     
	        fos.write(getData); 
	        if(fos!=null){
	            fos.close();  
	        }
	        if(inputStream!=null){
	            inputStream.close();
	        }
	        log.info("info:"+url+" download success"); 
	    }
	 public static  byte[] readInputStream(InputStream inputStream) throws IOException {  
	        byte[] buffer = new byte[1024];  
	        int len = 0;  
	        ByteArrayOutputStream bos = new ByteArrayOutputStream();  
	        while((len = inputStream.read(buffer)) != -1) {  
	            bos.write(buffer, 0, len);  
	        }  
	        bos.close();  
	        return bos.toByteArray();  
	    }  
  public static List<String> match(String source, String element, String attr) {
			List<String> result = new ArrayList<String>();
			String reg = "<" + element + "[^<>]*?\\s" + attr + "=['\"]?(.*?)['\"]?\\s.*?>";
			Matcher m = Pattern.compile(reg).matcher(source);
			while (m.find()) {
				String r = m.group(1);
				result.add(r);
			}
			return result;
	}
    public static Map<String,String> getCodeInfor(String li){
    	Map<String,String> data = new HashMap<String, String>();
        Parser parser = Parser.createParser(li, "gbk");	
        RegexFilter filter = new RegexFilter("[<a]*");
        try {
			NodeList tds = parser.extractAllNodesThatMatch(filter);
			Node a  = null ;
			String exchange = "" ;
			String code = "" ;
			String name = "" ;
			for(int i=0;i<tds.size();i++){
				try {
					a = tds.elementAt(i);
					if(a.getParent()!=null){
						String aHtml = a.getParent().toHtml();//<a target='_blank' href='http://quote.eastmoney.com/sz300447.html' >全信股份(300447)</a>
						List<String> list = match(aHtml.replace(">", " >"), "a","href");
						String href = list.get(0);//http://quote.eastmoney.com/sz300447.html
						String codeInfor = href.substring(
								href.lastIndexOf("/") + 1, href.length() - 5);
						exchange = codeInfor.substring(0, 2);
						code = codeInfor.substring(2, codeInfor.length());
					}
					String aTips = a.toHtml();//全信股份(300447)
					name = aTips.substring(0, aTips.lastIndexOf("("));
					if(code!=null&&!"".equals(code)&&!"null".equals(code)){
						data.put("name", name);
						data.put("code", code);
						data.put("exchange", exchange);
					}
					
				} catch (Exception e) {
//					e.printStackTrace();
					/*if(a!=null){
						log.info("###"+a.toHtml()+"处理异常！");
					}*/
				}
			}
		} catch (ParserException e) {
			e.printStackTrace();
		}
        return data;
    }
    public static String grabJsonDataFromURL(String urlStr){
    	URL url =null;  
        HttpURLConnection conn = null;
        InputStream inputStream = null;
        BufferedReader reader = null;
        String data = "";
		try {
			url = new URL(urlStr);  
			conn = (HttpURLConnection)url.openConnection();
			 //设置超时间为3秒
	        conn.setConnectTimeout(3*1000);
	        conn.setRequestProperty("accept", "*/*"); 
	        conn.setRequestProperty("user-agent","Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1)");
			//得到输入流
            inputStream = conn.getInputStream();  
            reader  = new BufferedReader(new InputStreamReader(inputStream,"GBK"));
            data = reader.readLine();
		} catch (Exception e) {
			log.info("Ocur exception ! url="+urlStr+",exc infor is "+e.toString());
		}  
    	return data;
    }
	public static void main(String[] args) {
		/*String str = "<li><a target=_blank href=http://quote.eastmoney.com/sz300498.html>温氏股份(300498)</a></li>";
		Map<String,String> data = getCodeInfor(str);
		String data0 ="var hq_str_sz300498=\"温氏股份,47.750,47.820,46.560,47.750,46.270,46.550,46.560,3578083,167478143.410,7300,46.550,17435,46.540,200,46.510,7300,46.500,400,46.480,900,46.560,1700,46.590,6200,46.600,2100,46.620,400,46.630,2015-12-24,11:35:55,00\"";
		log.info(data0.replace("\"", ""));
		String da = data0.replace("\"", "").split("=")[1];
		log.info(da);*/
		//grabCodeFromCFCenter();
		/*log.info(System.getProperty("user.dir")+"\\src\\main\\java\\com\\cyb\\qutoes\\utils");
		resolveCodeFromHtmlFile();*/
		/*String str ="var hq_str_sz000020=深华发Ａ,";
		System.out.println(str.split("=")[0].substring(str.split("=")[0].length()-8, str.split("=")[0].length()));
		System.out.println(str.split("=")[1].split(",")[0]);*/
		//grabShStock();
		//System.out.println("sh600868".substring(2, "sh600868".length()));
		System.out.println(path1);
		String data = FileUtils.readTxtFile(path1,"utf-8");
		JSONArray arr = JSONArray.fromObject(data);
		System.out.println();
	}
	public void insertStocks(){
		GrabDataDaoImpl dao = (GrabDataDaoImpl) SpringUtil.getBean("grabDataDao");
		System.out.println(dao);
		//FileUtils.readFileByBytes(fileName);
	}
	/**
	 * 根据新浪接口抓取上海股票代码
	 */
	public static void grabShStock(){
		stocks1 = new ArrayList<Map<String, Object>>();
		String str = "http://hq.sinajs.cn/list=sh";
		Map<String,Object> tmp =null;
		for(int i=0;i<=999999;i++){
			String stri =String.valueOf(i) ;
			String param = getZeroStr(6-stri.length())+stri;
			System.out.println(str+param);
		    String data = grabJsonDataFromURL(str+param);
		    data  = data.replace(";", "");
		    String key = data.split("=")[0].substring(data.split("=")[0].length()-8, data.split("=")[0].length());
			String name = new String(data.split("=")[1].split(",")[0]);
			if(!name.equals("\"\"")){//StringUtils.isNotEmpty(name)
			    tmp = new HashMap<String,Object>();
				name = name.substring(1);
				tmp.put("code", key.substring(2, key.length()));
				tmp.put("jys", key.substring(0,2));
				tmp.put("name", name);
				stocks1.add(tmp);
			}
		}
		String res = JSONArray.fromObject(stocks1).toString();
		FileUtils.writeString2File(res, path1);
		System.out.println(res);
	}
	public static String getZeroStr(int len){
		String str = "";
		for(int i=0;i<len;i++){
			str+="0";
		}
		return str;
	}
	/**
	 * 根据新浪接口抓取上海股票代码
	 */
	public static void grabSzStock(){
		stocks2 = new ArrayList<Map<String,Object>>();
		String str = "http://hq.sinajs.cn/list=sz";
		Map<String,Object> tmp =null;
		for(int i=0;i<=999999;i++){
			String stri =String.valueOf(i) ;
			String param = getZeroStr(6-stri.length())+stri;
			System.out.println(str+param);
		    String data = grabJsonDataFromURL(str+param);
		    data  = data.replace(";", "");
		    String key = data.split("=")[0].substring(data.split("=")[0].length()-8, data.split("=")[0].length());
			String name = new String(data.split("=")[1].split(",")[0]);
			if(!name.equals("\"\"")){//StringUtils.isNotEmpty(name)
				    name = name.substring(1);
				    tmp = new HashMap<String,Object>();
					name = name.substring(1);
					tmp.put("code", key.substring(2, key.length()));
					tmp.put("jys", key.substring(0,2));
					tmp.put("name", name);
					stocks2.add(tmp);
			}
		}
		String res = JSONArray.fromObject(stocks2).toString();
		FileUtils.writeString2File(res, path2);
		System.out.println(res);
	
	}
}
