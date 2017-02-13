package com.cyb.qutoes.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;
import javax.servlet.http.HttpServletRequest;

import net.sf.json.JSONArray;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.quartz.PushManager;
import com.cyb.qutoes.service.GrabDataService;
import com.cyb.utils.Contants;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.SpringUtil;

@Controller
@RequestMapping("qutoes")
public class QController {
	Log log = LogFactory.getLog(QController.class);
	static ExecutorService worker = null;
	static boolean grabFlag = false;
	@Resource(name = "grabDataService")
	public GrabDataService service;
	@Resource
	public JmsTemplate jmsTemplate;
	@RequestMapping(value="/test")
    @ResponseBody
	public JSONArray testt(HttpServletRequest request){
		log.info("*************");
		Map map = new HashMap();
		map.put("test", "infor");
		return JSONArray.fromObject(map);   
	}
	@RequestMapping(value="/close")
    @ResponseBody
	public JSONArray m1(HttpServletRequest request){
		log.info("手动导入收盘行情！");
    	Map map = new HashMap();
    	try{
    		 GrabDataDao dao = (GrabDataDao) SpringUtil.getBean("grabDataDao");
    		 dao.persisCloseQutoes("");
			 map.put("status", "success");
    	}catch(Exception e){
    		map.put("status", "fail");
    		e.printStackTrace();
    	}
    	return JSONArray.fromObject(map);   
   }
    @RequestMapping(value="/initStock")
    @ResponseBody
	public JSONArray initStock(){
    	log.info("手动导入基金、股票，债券代码！");
    	Map map = new HashMap();
    	try{
	    	 GrabDataDao dao = (GrabDataDao) SpringUtil.getBean("grabDataDao");
			 dao.saveCodeInfor();
			 map.put("status", "success");
    	}catch(Exception e){
    		map.put("status", "fail");
    		log.info("初始化股票代码异常！");
    	}
    	return JSONArray.fromObject(map);   
   }
    @RequestMapping(value="/infor")
    @ResponseBody
	public JSONArray pushServerStatus(){
		Map map = new LinkedHashMap();
		map.put("Push server status", PushManager.pushServerState);
		map.put("switch", QutoesContants.SWTICH);
		map.put("isholiday",QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString()));
		map.put("stocks", Contants.STOCKLIST);
		return JSONArray.fromObject(map);   
    }
    @RequestMapping(value="/shutdown")
    @ResponseBody
	public String closeQutoesServer(){
    	grabFlag = false;
    	if(worker!=null&&!worker.isShutdown()){
    		worker.shutdown();
    	}
    	grabFlag = false;
    	return "success";
    }
    public void shutdown(){
    	grabFlag=false;
    	if(worker!=null&&!worker.isShutdown()){
    		worker.shutdown();
    	}
    	worker = null;
    }
    @RequestMapping(value="/startup")
    @ResponseBody
	public String openQutoesServer(){
    	shutdown();
    	worker = Executors.newFixedThreadPool(10);
    	Runnable task  = new Runnable() {
			public void run() {
				grabFlag = true;
				while(grabFlag){
				try {
					service.grabQutoes();
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
			}
		};
		worker.submit(task);
		worker.submit(new MessageReceiver());
		return "success";
    }
    @RequestMapping(value="/productor")
    @ResponseBody
	public String createProductor(){
    	shutdown();
    	worker = Executors.newFixedThreadPool(10);
    	Runnable task  = new Runnable() {
			public void run() {
				grabFlag = true;
				while(grabFlag){
				try {
					service.grabQutoes();
					Thread.sleep(1000);
				} catch (Exception e) {
					e.printStackTrace();
				}
				}
			}
		};
		worker.submit(task);
		return "success";
    }
    @RequestMapping(value="/consumer")
    @ResponseBody
	public String createConsumer(){
    	shutdown();
    	worker = Executors.newFixedThreadPool(10);
		worker.submit(new MessageReceiver());
		return "success";
    }
    class MessageReceiver implements Runnable{
		@Override
		public void run() {
		try{
			String url = "tcp://localhost:61616";
			url = PropertyUtil.get("activemqUrl");
		    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connectionFactory.setUserName("admin");
	        connectionFactory.setPassword("amdin");
	        Connection connection = connectionFactory.createConnection();
	        connection.start();
	        Session session = connection.createSession(true,Session.AUTO_ACKNOWLEDGE);
	        Destination destination = session.createQueue("qutoesQueue");
	        MessageConsumer consumer = session.createConsumer(destination);
	        int count = 0;
	        grabFlag = true;
			while(grabFlag){
				try {
					MapMessage message = (MapMessage)consumer.receive();  
					count++;
					if(message.getString("code")!=null&&!"".equals(message.getString("code"))){	
						 if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
							 if(PropertyUtil.get("testcode").equals(message.getString("code"))){
							     log.info("[{SendTime=" +message.getString("sendTime")+",GetTime=" + DateUtil.timeToMilis(new Date()));
							     log.info(Thread.currentThread().getName()+",recive case:"+message.getString("qutoes"));
							 }
						}
					}
					if(count>5000){
						count = 0;
		            	session.commit();
		            }
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			session.commit();
			connection.stop();
			connection.close();
			session.close();
			consumer.close();
			connection = null;
			session = null;
			consumer = null;
		}catch (Exception e) {
			e.printStackTrace();
		}
		}   	
    }
}
