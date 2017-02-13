package com.cyb.mulitthread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.dao.UncategorizedDataAccessException;

public class MyCallable  implements  Callable<List>{
	private int count =10;
	private String oid; 
    private Object obj = new Object();
    MyCallable(String oid) { 
            this.oid = oid; 
    } 
	public List call() throws Exception {
		String str =this.oid; 
		List lst = new ArrayList<String>();
		synchronized (obj) {
	//		for(int i=0;i<5;i++){}
			while(count>0){
				System.out.println(count--);
	//			Thread.sleep(1000);
				str = str+count+",";  
				lst.add(count);
			}
		}
		return lst;
	}
	 public static void main(String[] args) throws InterruptedException, ExecutionException {
		//����һ���̳߳� 
         ExecutorService pool = Executors.newFixedThreadPool(2); 
         //���������з���ֵ������ 
         Callable c1 = new MyCallable("A"); 
         Callable c2 = new MyCallable("B"); 
         Callable c3 = new MyCallable("C"); 
         Callable c4 = new MyCallable("D"); 
         //ִ�����񲢻�ȡFuture���� 
         /*�������񹲺�count 10��*/  
         Future f1 = pool.submit(c1); 
         Future f2 = pool.submit(c1); 
         Future f3 = pool.submit(c1); 
         Future f4 = pool.submit(c1); 
//         JdbcTemplate x;
         BasicDataSource y;
         UncategorizedDataAccessException e;
//         f1.cancel(false);
         /*Future f1 = pool.submit(c1); 
         Future f2 = pool.submit(c2); 
         Future f3 = pool.submit(c3); 
         Future f4 = pool.submit(c4);*/ //û���������ĵ�10��count
         //��Future�����ϻ�ȡ����ķ���ֵ�������������̨ 
         System.out.println(new Date()+">>>"+f1.get().toString()); 
         System.out.println(new Date()+">>>"+f2.get().toString()); 
         System.out.println(new Date()+">>>"+f3.get().toString()); 
         System.out.println(new Date()+">>>"+f4.get().toString());
         //�ر��̳߳� 
         pool.shutdown(); 
	}
}
