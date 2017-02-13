package com.cyb.jms.spring;

import java.util.Date;

import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.jms.core.MessageCreator;

import com.cyb.jms.ActiveMqManager;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class QutoesMessage implements MessageCreator {
	Logger log = Logger.getLogger(QutoesMessage.class);
	public String code;
	public String qutoes;
    public QutoesMessage(String code,String qutoes){
    	this.code = code;
    	this.qutoes = qutoes;
    	
    }
	@Override
	public Message createMessage(Session session) throws JMSException {
		 MapMessage message=session.createMapMessage();  
		 message.setString("qutoes", qutoes);
		 message.setString("code", code);
		 message.setString("sendTime", DateUtil.timeToMilis(new Date()));
		 if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
			 if(PropertyUtil.get("testcode").equals(code)){
				 
			 }
		}
		return session.createMapMessage();
	}

}
