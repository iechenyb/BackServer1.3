package com.cyb.qutoes.quartz;

import com.cyb.utils.PropertyUtil;

public class TestDeadLock implements Runnable{  
	String name = "";
	public TestDeadLock(String name){
	   this.name = name;	
	}
    public String has = "knife"; // 1 knife and 2 fork;
    static Object knife = new Object(), fork = new Object();  
    public static void main(String[] argv){  
        TestDeadLock td1 = new TestDeadLock("task1");  
        TestDeadLock td2 = new TestDeadLock("task2");  
        td1.has = "knife";  
        td2.has = "fork";  
        Thread t1 = new Thread(td1);  
        Thread t2 = new Thread(td2);  
        t1.start();  
        t2.start();  
    }  
    public static void kaishi(){
    	if(Boolean.valueOf(PropertyUtil.get("testDeadLock"))){
	    	System.out.println("准备开启死锁线程！");
	    	TestDeadLock td1 = new TestDeadLock("TestDeadLock-task1");  
	        TestDeadLock td2 = new TestDeadLock("TestDeadLock-task2");  
	        td1.has = "knife";  
	        td2.has = "fork";  
	        Thread t1 = new Thread(td1);  
	        Thread t2 = new Thread(td2);  
	        t1.start();  
	        t2.start();  
        }
    }
    public void run(){
    	Thread.currentThread().setName(name);
    	System.out.println(name +" start:has "+ has);  
        if("knife".equals(has)){  //task1 has knife,need fork to eat!
            synchronized (knife){ 
            	System.out.println(name+" wait for to get fork...");
                try{  
                    Thread.sleep(500);  
                }catch(Exception e){  
                    e.printStackTrace();  
                }  
                synchronized(fork){  
                    System.out.println("knife");  
                }  
            }  
        }  
        if("fork".equals(has)){//task2 has fork, need knife to eat!  
            synchronized(fork){  
            	System.out.println(name+" wait for to get knife...");
                try{  
                    Thread.sleep(500);  
                }catch(Exception e){  
                    e.printStackTrace();  
                }  
                synchronized(knife){  
                    System.out.println("knife");  
                }  
            }  
        }  
        System.out.println(name +" end ,has "+ has);  
    }  
}  
