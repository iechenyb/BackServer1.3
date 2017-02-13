package com.cyb.mbean;

import com.cyb.activemq.MessageReceiver;
import com.cyb.activemq.MessageSender;

public class ActiveMqQueueBean {
  public void 启动生产者(){
	  new Thread(new MessageSender()).start();
  }
  public void 启动消费者(){
	  new Thread(new MessageReceiver()).start();
	  new Thread(new MessageReceiver()).start();
  }
  public void 关闭生产者(){
	  MessageSender.start = false;
  }
  public void 关闭消费者(){
	  MessageReceiver.start=false;
  }
}
