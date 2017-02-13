package com.cyb.jms.service;

import java.util.Date;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Component;

import com.cyb.jms.ActiveMqManager;
import com.cyb.utils.DateUtil;
@Component("producerServiceImpl")
public class ProducerServiceImpl  {
	 private JmsTemplate jmsTemplate;  
	 
	 public void sendTextMessage(Destination destination, final String message) {   
	        System.out.println("JMS发了一个消息：" + message);   
	        jmsTemplate.send(destination, new MessageCreator() {   
	            public Message createMessage(Session session) throws JMSException {   
	            	TextMessage msg = session.createTextMessage(message);
	                return msg;   
	            }   
	        });  
	  }   
	 public void sendMapMessage(Destination destination, final String message) {   
	        System.out.println("JMS发了一个消息：" + message);   
	        jmsTemplate.send(destination, new MessageCreator() {   
	            public Message createMessage(Session session) throws JMSException {   
	            	 MapMessage message=session.createMapMessage();  
	            	 message.setJMSDestination(ActiveMqManager.destination);
					 message.setString("qutoes", "########################################################################################################################################");
					 message.setString("code", "SDFSDFSDFSDDDDDDDDDDD");
					 message.setString("sendTime", DateUtil.timeToMilis(new Date()));
	                return message;   
	            }   
	        });  
	  }  
	  public void receiveMessage(Destination destination, final String message) {   
	        jmsTemplate.receive();  
	    }    
	    public JmsTemplate getJmsTemplate() {   
	        return jmsTemplate;   
	    }    
	  
	    @Resource  
	    public void setJmsTemplate(JmsTemplate jmsTemplate) {   
	        this.jmsTemplate = jmsTemplate;   
	    }   
}
