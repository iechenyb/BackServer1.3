package com.cyb.qutoes.service.impl;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import com.cyb.jms.spring.ActiveMqManager;
import com.cyb.page.Pagination;
import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.mapper.RealQutoesMapper;
import com.cyb.qutoes.service.GrabDataService;
import com.cyb.utils.Contants;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.UrlUtils;

@Service("grabDataService")
public class GrabDataServiceImpl implements GrabDataService{
	public static Logger log = Logger.getLogger(GrabDataServiceImpl.class);
	@Resource(name = "grabDataDao")
	public GrabDataDao dao;
	@Resource
	private JmsTemplate jmsTemplate; 
	public void saveCodeInfor() {
		this.dao.saveCodeInfor();
	}
	public List getAllCodeInfor() {
		return this.dao.getAllCodeInfor();
	}
	public void persisMinQutoes(List<Map<String, Object>> data) {
		this.dao.persisMinQutoesBatch( data);
	}
	public List getAllDayQutoes(String stockCode) {
		return this.dao.getAllDayQutoes(stockCode);
	}
	public void grabQutoes(){
		int count = 0;
		long s = System.currentTimeMillis();
			try{
				int pageSize = Integer.valueOf(PropertyUtil.get("drawBatchNum"));
				try{
					String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
					long total = Contants.STOCKLIST.size();
					if(Contants.STOCKLIST!=null&&total>0){
						StringBuffer codes = null;
						Pagination page= new Pagination(1,pageSize,total);
						int pageCount = page.getPageCount();
						for(int j=1;j<=pageCount;j++){//分页处理
							List<MessageCreator> msgs = new ArrayList<MessageCreator>();
							codes = new StringBuffer("");
							Pagination page_= new Pagination(j,pageSize,total);
							long max = page_.getPageSize()*j-1;
							if(max >=total){
								max = total-1;
							}
							log.debug(page_.getOffset()+","+max);
							for(int i=page_.getOffset();i<max;i++){
							   codes.append(Contants.STOCKLIST.get(i).get("CODE").toString()+",");
							}
							String url = qutoesUrl+codes.toString();
							InputStream is = UrlUtils.getStream(url);
							BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
				            String qutoes = ""; 
				            String codeInfo ="";
				            while((qutoes=buffer.readLine())!=null){
				            	try{
					            	qutoes = qutoes.replaceAll(" ", "");
					            	codeInfo = qutoes.split("=")[0];
					            	final String code = codeInfo.substring(10,codeInfo.length());
					            	final String qutoesInfo = qutoes.split("=")[1];
					            	int len = qutoes.split("=")[1].split(",").length;
					            	if(len>3){
					            		count++;
					            		log.debug("[normal]"+code+","+qutoesInfo);
					            		MessageCreator messageCreator = new MessageCreator() {  
								        public Message createMessage(Session session) throws JMSException { 
								            	 MapMessage message=session.createMapMessage();  
												 message.setString("qutoes", qutoesInfo);
												 message.setString("code", code);
												 message.setString("sendTime", DateUtil.timeToMilis(new Date()));
												 if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
													 if(PropertyUtil.get("testcode").equals(code)){
														log.info("send:"+code+","+message.getString("qutoes"));
													 }
												 }
								                return message;   
								            }
								        };
								        /*msgs.add(messageCreator);*/
					            		jmsTemplate.send(messageCreator);
					               }else{
					            	   log.debug("[error]"+qutoesInfo);
					               }
					            }catch(Exception e){
					            	log.info("行情["+qutoes+"]解析异常，可忽略！");
					            }
				            } 
				            is.close();
				            is = null;
				            codes.delete(0, codes.length());
				           /* for(int i=0;i<msgs.size();i++){
				            	jmsTemplate.send(msgs.get(i));
							}*/
						}//end 分页
					}//end not null if
				}catch(Exception e){
					log.info(e.toString());
				}
				long e = System.currentTimeMillis();
				log.debug("处理记录数="+count+",数据处理速度="+count+"/"+((e-s)/1000)+"."+(e-s)%1000+"条/秒.毫秒");
				if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
					log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒");
				}
			}catch(Exception e){}
	}
}
