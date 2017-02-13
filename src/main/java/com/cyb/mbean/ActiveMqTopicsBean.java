package com.cyb.mbean;

import com.cyb.activemq.MessageReceiver;
import com.cyb.activemq.ReceiveTopic;
import com.cyb.activemq.SendTopic;

public class ActiveMqTopicsBean {
  
  public void 启动生产者(){
	  SendTopic send= new SendTopic();
	  Thread t= new Thread(send);
	  t.start();
  }
  public void 启动消费者(){
	  ReceiveTopic receive1= new ReceiveTopic();
	  Thread t= new Thread(receive1);
	  t.start();
	  ReceiveTopic receive2= new ReceiveTopic();
	  Thread t1= new Thread(receive2);
	  t1.start();
  }
  public void 关闭生产者(){
	  SendTopic.start = false;
  }
  public void 关闭消费者(){
	  new Thread(new MessageReceiver()).start();
	  ReceiveTopic.start=false;
  }
}
