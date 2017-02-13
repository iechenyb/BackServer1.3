package com.cyb.test;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.TextMessage;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.cyb.jms.service.ProducerServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)   
@ContextConfiguration(locations = { "classpath*:applicationContext_base.xml","classpath*:applicationContext_activeMq.xml"})   
public class ProducerConsumerTest {   	    
	 @Resource(name="producerServiceImpl")
    private ProducerServiceImpl producerService;   
    @Resource(name="qutoesDestination")   
    private Destination destination;   
    @Resource
    private JmsTemplate jmsTemplate; 
    @Test 
    public void main(){
    	try {
			testMapSend();
			Thread.sleep(10);
			testReceive();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
    }
    @Test  
    public void testTextSend() {   
        for (int i=0; i<10; i++) {   
        	long s = System.currentTimeMillis();
            producerService.sendTextMessage(destination, "你好，生产者！这是消息：" + (i+1));   
            long e = System.currentTimeMillis();
			System.out.println("send one record spend time is "+(e-s)/1000+"s."+(e-s)%1000+"ms");
        }   
    }   
    @Test  
    public void testMapSend() {   
        for (int i=0; i<10; i++) {   
        	long s = System.currentTimeMillis();
            producerService.sendMapMessage(destination, "你好，生产者！这是消息map：" + (i+1));   
            long e = System.currentTimeMillis();
			System.out.println("send one record spend time is "+(e-s)/1000+"s."+(e-s)%1000+"ms");
        }   
    }   
    @Test  
    public void testReceive() {   
    	for(int i=0;i<10;i++){
    		 try {
	    	     TextMessage msg = (TextMessage) jmsTemplate.receive();
				 System.out.println("JMS接收一个消息:"+msg.getText());
			} catch (JMSException e) {
				e.printStackTrace();
			}
	    } 
    }  
    
}
