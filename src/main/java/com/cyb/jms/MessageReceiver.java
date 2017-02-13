package com.cyb.jms;

/*
 * http://activemq.apache.org/activemq-5133-release.html
 * http://blog.csdn.net/xh16319/article/details/12142249
   http://www.tuicool.com/articles/jABfEff
   http://127.0.0.1:8161/admin/
   Exception in thread "main" javax.jms.IllegalStateException: Cannot synchronously receive a message when a MessageListener is set
 */

import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.Connection;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.utils.PropertyUtil;

public class MessageReceiver implements Runnable{
	public static Logger log = Logger.getLogger(MessageReceiver.class);
	public GrabDataDao dao;
	public MessageReceiver(GrabDataDao dao){
		this.dao = dao;		
	}
	public void run() {
		Thread.currentThread().setName("activemq接收线程#");
		try {
			/* Session session = ActiveMqManager.connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			 Destination destination = ActiveMqManager.session.createQueue(PropertyUtil.get("qutoesQueueName"));*/
			 //MessageConsumer consumer = session.createConsumer(destination);
			//ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			String url = "tcp://localhost:61616";
		    ActiveMQConnectionFactory connectionFactory = new ActiveMQConnectionFactory(url);
		    connectionFactory.setUserName("admin");
	        connectionFactory.setPassword("amdin");
	        Connection connection = connectionFactory.createConnection();
			connection.start();
			Session session = connection.createSession(true,Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("qutoesQueue");
			MessageConsumer consumer = session.createConsumer(destination);
			log.info("消费者["+Thread.currentThread().getName()+"]启动成功");
			ThreadPoolExecutor work = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
			int count =0;
			while (true) {
			    try {		
			    	count++;
			    	Message message1 = consumer.receive(1000);  
			    	if(message1!=null){
						MapMessage message = (MapMessage)message1;
						log.debug("recive all:"+message);
						String code = message.getString("code");
						if(code!=null&&!"".equals(code)){		
							 if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
								 if(PropertyUtil.get("testcode").equals(code)){
								   log.info("recive case:"+message.getString("qutoes"));
							     }
							 }
						}
						Future<Boolean> result = work.submit(new SaveQutoesTask(dao, message));
						boolean flag = result.get();
						if(flag){
							if(count>5000){
							   session.commit();
							}
						}
						
			    	}
				} catch (Exception e) {
					log.info(e.toString());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		}
	}
}