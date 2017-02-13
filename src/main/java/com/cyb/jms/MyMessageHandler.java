package com.cyb.jms;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;

import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class MyMessageHandler implements Runnable {
	public static Logger log = Logger.getLogger(MyMessageHandler.class);
	Map<String,String> data = new HashMap<String,String>();
	public MyMessageHandler(Map<String,String> data){
		this.data =data;
	} 
	@Override
	public void run() {
		try {					
			MapMessage message = ActiveMqManager.session.createMapMessage();
			message.setJMSDestination(ActiveMqManager.destination);
			message.setString("qutoes", data.get("qutoes"));
			message.setString("code", data.get("code"));
			message.setString("sendTime", DateUtil.timeToMilis(new Date()));
			if(data.get("code").equals("sh600868")){
				log.info("send:"+message);
			}
			log.info("send all:"+message);
			//ActiveMqManager.producer.send(message);
			ActiveMqManager.producer.send(message);
			ActiveMqManager.session.commit();
		} catch (JMSException e) {
			log.info(e.toString());
			e.printStackTrace();
		}
	}

}
