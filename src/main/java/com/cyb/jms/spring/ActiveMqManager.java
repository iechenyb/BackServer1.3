package com.cyb.jms.spring;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.DeliveryMode;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.log4j.Logger;

import com.cyb.qutoes.utils.GrabEntity;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
public class ActiveMqManager {
	public static Logger log = Logger.getLogger(ActiveMqManager.class);
	public static PooledConnectionFactory pooledConnectionFactory = null;
	public static  ActiveMQConnectionFactory connectionFactory = null;
	public static Session session = null;
	public static  MessageProducer producer = null ; 
	public static MessageConsumer consumer = null;
	public static Destination destination = null;
	public static Connection connection = null;
	public synchronized static void initQutoesQueue(){
    try {
			  connection = pooledConnectionFactory.createConnection();
			  connection.start();
			  session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			  destination = session.createQueue(PropertyUtil.get("qutoesQueueName"));
			  
		         //ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");//61616
		          connection = connectionFactory.createConnection();
		         //connection.setExceptionListener(new MyJmsException());
		         connection.start();
		          session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		          destination = session.createQueue(PropertyUtil.get("qutoesQueueName"));
		         session = connection.createSession( true, Session. AUTO_ACKNOWLEDGE);
		         destination = session.createTopic("FirstTopic");
			     log.info("行情队列信息初始化成功！");
		} catch (JMSException e) {
			log.info(e.toString());
			e.printStackTrace();
		}
	}
	public  static MessageProducer createProducer(){
		 try {
			    connectionFactory = new ActiveMQConnectionFactory("tcp://192.168.16.211:61616");
			    connectionFactory.setUserName("admin");
	            connectionFactory.setPassword("admin");
			    connection = connectionFactory.createConnection();
		        connection.start();
		        session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
		        destination = session.createQueue("qutoesQueue");
		        producer  = session.createProducer(destination);
		       // producer.setDeliveryMode(DeliveryMode.PERSISTENT);
		        log.info("创建生产者："+producer);
		} catch (JMSException e) {
			log.info(e.toString());
			e.printStackTrace();
		}
		 return producer;
	}
	public synchronized  static MessageConsumer createConsumer(){
		try {
			destination = session.createQueue(PropertyUtil.get("qutoesQueueName"));
			consumer = session.createConsumer(destination);
		} catch (JMSException e) {
			log.info(e.toString());
			e.printStackTrace();
		}
		 return consumer;
	}
	
	public static void createQutoesMessage(){
		try{		
		 ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");//61616
         connectionFactory.setUserName("admin");
         connectionFactory.setPassword("amdin");
         javax.jms.Connection connection = connectionFactory.createConnection();
         connection.start();
         Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
         Destination destination = session.createQueue("qutoes");
         MessageProducer producer = session.createProducer(destination);
        for(int i=0;i<100;i++){
	        String retData = GrabEntity.grabJsonDataFromURL("http://hq.sinajs.cn/list=sh600868");				
	 		retData = retData.replace("\"", "");
	 		String dataStr = retData.split("=")[1];
			 if(!";".equals(dataStr)){
				Map<String,String> data = new HashMap<String,String>();
				data.put("qutoes", dataStr);
				data.put("code", "sh600868");
				MapMessage message = session.createMapMessage();
				message.setString("qutoes", data.get("qutoes"));
				message.setString("code", data.get("code"));
				message.setString("sendTime", DateUtil.timeToMilis(new Date()));
				producer.send(message);
				session.commit();
			}
		}
		 session.close();
         connection.close();
	 }catch(Exception e){
		 try {
			 connection.close();
			session.close();
		} catch (JMSException e1) {
			e1.printStackTrace();
		}
	 }
	}
	public static void main(String[] args) {
		createQutoesMessage();
	}
}
