package com.cyb.mbean.utils;

import com.cyb.qutoes.quartz.GrabQutoesShecdualJob;
import com.cyb.utils.SpringUtil;

public class GrabQutoesThread implements  Runnable {
	private static ThreadLocal<Boolean> startHandler = new ThreadLocal<Boolean>() { 
		 @Override  
	        protected Boolean initialValue() {  
			 return false;
		 }
	};
	public void run() {
		setStart(true);
		GrabQutoesShecdualJob job = (GrabQutoesShecdualJob) SpringUtil.wac.getBean("grabQutoesShecdualJob");
		System.out.println(Thread.currentThread().getName()+"抓取行情任务开始");
		while(getStart()){
			job.grabRealQutoes1();
		}
		setStart(false);
		System.out.println(Thread.currentThread().getName()+"抓取行情任务结束");
	}
	public static void setStart(boolean start) {
		startHandler.set(start);
	}
	
	public static boolean getStart() {
		return startHandler.get();
	}
}
