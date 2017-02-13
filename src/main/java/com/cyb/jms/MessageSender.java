package com.cyb.jms;

/*
 * xcch@cnic.cn
 * ��Ϣ������
 */
import java.util.Date;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
public class MessageSender  implements Runnable{
	public static Logger log = Logger.getLogger(MessageSender.class);
	public void run() {
		Thread.currentThread().setName("activemq消息生产者");
		 try {
			 ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(PropertyUtil.get("activemqUrl"));//wireFormat.maxInactivityDurationInitalDelay=30000
			 javax.jms.Connection connection = connectionFactory.createConnection();
			 connection.start();
			 Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			 Destination destination = session.createQueue("my-queue");
			 MessageProducer producer = session.createProducer(destination);
			 log.info("生产者开始产生消息！");
			 while(true){
			     MapMessage message = session.createMapMessage();
			     long time = new Date().getTime();
			     message.setLong("count", time);
			     log.info("activemq create a msg  is "+time);
			     producer.send(message);
			     session.commit();
			 }
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
			 /*session.commit();
			 session.close();
			 connection.close();*/
		}
	} 
     
}
