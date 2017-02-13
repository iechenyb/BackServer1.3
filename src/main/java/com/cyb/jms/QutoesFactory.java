package com.cyb.jms;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.log4j.Logger;

import com.cyb.qutoes.utils.GrabEntity;
import com.cyb.utils.Contants;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class QutoesFactory implements Runnable{
	public static Logger log = Logger.getLogger(QutoesFactory.class);
	@Override
	public void run() {
		ThreadPoolExecutor work = (ThreadPoolExecutor) Executors.newFixedThreadPool(3);
		while(true){
		   try{
			  log.info("计算开始...");
			  try {
				if(Contants.STOCKLIST!=null){
					persisMinQutoesBatch(Contants.STOCKLIST);
//					persisMinQutoesBatch(Contants.STOCKLIST.subList(1001,2000));
//					persisMinQutoesBatch(Contants.STOCKLIST.subList(2001, Contants.STOCKLIST.size()));
					/*Future<Boolean> result1 = work.submit(task1);
					Future<Boolean> result2 = work.submit(task2);
					Future<Boolean> result3 = work.submit(task3);
					log.info("task1:"+result1.get());
					log.info("task2:"+result2.get());
					log.info("task3:"+result3.get());*/
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("计算异常！");
			} finally{
				//work.shutdown();
			}
			   log.info("计算结束");
			   Thread.sleep(1000);
			   }catch(Exception e){
				   log.info(e.toString());
				   work.shutdown();
				   e.printStackTrace();
			   }
		}
	}
	public void persisMinQutoesBatch(List<Map<String, Object>> stocks){
		long start = System.currentTimeMillis();
		String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
		String code = "";
		String exchange = "";
		String retData = "";
		if(ActiveMqManager.producer==null){
			ActiveMqManager.createProducer();
		}
		int count = stocks.size();
		if(stocks!=null&&count>0){
			List<MapMessage> lst = new ArrayList<MapMessage>();
		    for(int i=0;i<count;i++){
			    long s = System.currentTimeMillis();
		        Map<String,Object> stock  = stocks.get(i);
			    try{
					code = stock.get("CODE").toString();		
					retData = GrabEntity.grabJsonDataFromURL(qutoesUrl+code);				
					retData = retData.replace("\"", "");
					String dataStr = retData.split("=")[1];
					if(!";".equals(dataStr)){
						Map<String,String> data = new HashMap<String,String>();
						data.put("qutoes", dataStr);
						data.put("code", code);
						MapMessage message = ActiveMqManager.session.createMapMessage();
						message.setJMSDestination(ActiveMqManager.destination);
						message.setString("qutoes", data.get("qutoes"));
						message.setString("code", data.get("code"));
						message.setString("sendTime", DateUtil.timeToMilis(new Date()));
						lst.add(message);
						if(data.get("code").equals("sh600868")){
							log.info("send:"+message.getString("qutoes"));
						}
						if((i%2000)==0){
							for(int j=0;j<lst.size();j++){
								ActiveMqManager.producer.send(lst.get(j));
							}
							//ActiveMqManager.session.commit();
						}
					} 
					long e = System.currentTimeMillis();
					log.info("["+(i+1)+"/"+count+"]条{"+(e-s)/1000+"s."+(e-s)%1000+"ms}记录处理over!");
				}catch(Exception e){
					log.info(qutoesUrl+exchange+code+"，数据抓取失败！"+e.toString());
				}
		  }
		    
		    if(lst.size()>0){
				for(int j=0;j<lst.size();j++){
					try {
						ActiveMqManager.producer.send(lst.get(j));
					} catch (JMSException e) {
						e.printStackTrace();
					}
				}
			}
		    try {
				ActiveMqManager.session.commit();
			} catch (JMSException e) {
				e.printStackTrace();
			}
		}
	long end = System.currentTimeMillis();
	log.info("用时共计"+(end-start)/1000+"s");
	}
}
	
