package com.cyb.mulitthread;

import com.cyb.ws.SystemWebSocketHandler;

public class TestThread implements Runnable{
public int i =0 ;
public TestThread(){
	System.out.println("�����߳�������");
}
	public void run() {
		while(true){
			System.out.println(i);
			SystemWebSocketHandler.sendMessageToUsers("text","chenyb"+i++);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
