package com.cyb.push;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PushPointThread  implements Runnable{
	public static Boolean start = false;
	public PushPointThread(boolean start){
		PushPointThread.start = start;
	}
	Logger log = LoggerFactory.getLogger(PushPointThread.class);
	public void run() {
		Thread.currentThread().setName("点消息推送线程");
		System.out.println("点推送开始");
		while (start) {
			try {
				int x = new Random().nextInt(255);
				int y = new Random().nextInt(255);
				Map<String,Integer> map = new HashMap<String,Integer>();
				map.put("x", x+1);
				map.put("y", y+1);
				PushServer.sendBroadcast("point",map);
				System.out.println("push the point:"+map);
				Thread.sleep(2*1000);
			} catch (Exception e) {
				log.info("push logic ocur exception, don't care....");
			}
		}
		System.out.println("点推送结束");
	}
	public boolean stop(){
		start = false;
		return false;
	}
}
