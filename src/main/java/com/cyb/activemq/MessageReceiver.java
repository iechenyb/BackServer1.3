package com.cyb.activemq;

/*
 * http://activemq.apache.org/activemq-5133-release.html
 * http://blog.csdn.net/xh16319/article/details/12142249
   http://www.tuicool.com/articles/jABfEff
   http://127.0.0.1:8161/admin/
   Exception in thread "main" javax.jms.IllegalStateException: Cannot synchronously receive a message when a MessageListener is set
 */

import java.util.Date;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
public class MessageReceiver implements Runnable{
	public static Logger log = Logger.getLogger(MessageReceiver.class);
    public static boolean start = false;
	@Override
	public void run() {
		try {
			start = true;
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory();
			Connection connection = connectionFactory.createConnection();
			connection.start();
			final Session session = connection.createSession(true,Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue("my-queue");
			MessageConsumer consumer = session.createConsumer(destination);
			log.info("消费者已启动！");
			while (start) {
			    MapMessage message = (MapMessage) consumer.receive();
			    session.commit();
			    Thread.sleep(1000);
			    log.info(Thread.currentThread().getName()+" "
			    		+ "消耗掉消息 " + message.getLong("count"));
			}
			session.close();
			connection.close();
			log.info("消费者已结束！");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}