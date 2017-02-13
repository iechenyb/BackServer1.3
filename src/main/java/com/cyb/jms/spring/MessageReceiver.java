package com.cyb.jms.spring;

//http://blog.csdn.net/u011325787/article/details/51421377  
//http://blog.csdn.net/haoxingfeng/article/details/9167895

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.jms.MapMessage;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;

import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class MessageReceiver implements Runnable{
	public static Logger log = Logger.getLogger(MessageReceiver.class);
	public JmsTemplate jmsTemplate;
	public GrabDataDao grabDataDao;
	public MessageReceiver(JmsTemplate jmsTemplate,GrabDataDao grabDataDao){
		this.grabDataDao = grabDataDao;
		this.jmsTemplate = jmsTemplate;		
	}
	public void run() {
		try {
			int count = 0;
			List<String> sqls = new ArrayList<String>();
			String infor = "";
			Thread.currentThread().setName("activemq接收线程");
			while (true) {
			    try {		
			    	MapMessage message = (MapMessage)jmsTemplate.receive();  
			    	infor = message.getString("qutoes");
					if(message.getString("code")!=null&&!"".equals(message.getString("code"))){	
						 if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
							 if(PropertyUtil.get("testcode").equals(message.getString("code"))){
							     log.info("[{SendTime=" +message.getString("sendTime")+",GetTime=" + DateUtil.timeToMilis(new Date()));
							     log.info(Thread.currentThread().getName()+",recive case:"+message.getString("qutoes"));
							 }
						}
					}
					/*if(infor.split(",").length>3){
						count++;
						final RealQutoes realQutoes = RealQutoesMapper.MapMessage2Object(message);
						if(!QutoesContants.realQutoesMap.containsKey(realQutoes.getCode())){
							sqls.add(grabDataDao.saveRealQutoes(realQutoes));
							QutoesContants.realQutoesMap.put(message.getString("code"), realQutoes);
						}else{
							sqls.add(grabDataDao.updateRealQutoes(realQutoes));
						}
					}
					if(count>=1000){
						long s = System.currentTimeMillis();
					    count=0;
						grabDataDao.exeSqls(sqls);
						sqls.clear();
						sqls = new ArrayList<String>();
						long e = System.currentTimeMillis();
						log.info("批量执行sql结束！"+(e-s)/1000+"s."+((e-s)%1000)+"ms");
					}*/
				} catch (Exception e) {
					log.error(infor+"->"+e.toString());
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		}
	}
}