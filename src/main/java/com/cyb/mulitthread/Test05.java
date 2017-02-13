package com.cyb.mulitthread;

import java.util.List;
import java.util.Properties;

import javax.naming.InitialContext;
import javax.naming.NamingException;
class AddThread implements Runnable {
    private List<Double> list;

    public AddThread(List<Double> list) {
        this.list = list;
    }

    public void run() {
        for(int i = 0; i < 10000; ++i) {
            list.add(Math.random());
            System.out.println(Thread.currentThread().getName());
        }
    }
}

public class Test05 {
    private static final int THREAD_POOL_SIZE = 2;

    public static void main(String[] args) throws NamingException {
      /*  List<Double> list = new CopyOnWriteArrayList<Double>();
        ExecutorService es = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        es.execute(new AddThread(list));
        es.execute(new AddThread(list));
        es.shutdown();*/
    	Properties props = new Properties();
        props.setProperty("java.naming.factory.initial", "org.jnp.interfaces.NamingContextFactory");
        props.setProperty("java.naming.provider.url", "localhost:1099");

        try {
            InitialContext ctx = new InitialContext(props);
//            InitialContext ctx = new InitialContext(); 
	        ctx.bind("db1", "ss");
	        Object txObj = ctx.lookup("db1"); 
        }catch(Exception e){
        	e.printStackTrace();
        }
        
//        UserTransaction utx = (UserTransaction) txObj;
    }
}