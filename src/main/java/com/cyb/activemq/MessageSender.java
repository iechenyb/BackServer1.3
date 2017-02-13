package com.cyb.activemq;

import java.util.Date;

import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
//ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");//61616
//connection.setExceptionListener(new MyJmsException());
public class MessageSender  implements Runnable{
	public static Logger log = Logger.getLogger(MessageSender.class);
	public static boolean start = false;
	@Override
	public void run() {
		 try {
			 start = true;
			 ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://localhost:61616");//wireFormat.maxInactivityDurationInitalDelay=30000
			 javax.jms.Connection connection = connectionFactory.createConnection();
			 connection.start();
			 Session session = connection.createSession(true, Session.AUTO_ACKNOWLEDGE);
			 Destination destination = session.createQueue("my-queue");
			 MessageProducer producer = session.createProducer(destination);
			 log.info("生产者开始产生消息！");
			 int count =0;
			 while(start){
				 count++;
			     MapMessage message = session.createMapMessage();
			     message.setLong("count", count);
			     System.out.println(Thread.currentThread().getName()+"生成消息："+count);
			     Thread.sleep(1000);
			     producer.send(message);
			     session.commit();
			 }
			 count=0;
			 session.close();
			 connection.close();
			 log.info("生产者产生消息任务结束！");
		} catch (Exception e) {
			e.printStackTrace();
		} finally{
		}
	} 
     
}
