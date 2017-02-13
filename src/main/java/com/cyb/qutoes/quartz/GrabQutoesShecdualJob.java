package com.cyb.qutoes.quartz;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.Session;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cyb.page.Pagination;
import com.cyb.push.PushConstant;
import com.cyb.push.PushServer;
import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.mapper.RealQutoesMapper;
import com.cyb.qutoes.utils.GrabEntity;
import com.cyb.utils.Contants;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.SpringUtil;
import com.cyb.utils.UrlUtils;

@Component
public class GrabQutoesShecdualJob {
	Log  log = LogFactory.getLog(GrabQutoesShecdualJob.class);
	@Resource
	JdbcTemplate jdbcTemplate;
	@Resource
	private JmsTemplate jmsTemplate;  
	@Resource
	public GrabDataDao grabDataDao;
	@Resource(name="qutoesDestination")  
    private Destination destination;  
	
    public static Map<String,Object> dataSource = new HashMap<String,Object>();
    String multiSql = "delete from REALTIMEQUTOES  where id_ in( SELECT max(id_) FROM REALTIMEQUTOES "
    		+ " group by code_ having count(code_)>1)";
	
	@Scheduled(cron="* * 05-12,13-15 ? * SUN-SAT")
	public void grabRealQutoes1(){
		Thread.currentThread().setName("行情抓取线程");
		 log.debug("计算开始");
		 grabDataDao.exeSql(multiSql);
		 if(Boolean.valueOf(PropertyUtil.get("batch"))){
			 minQutoesThreadBatch2();
		 }else{
			 minQutoesThreadOneByOne();
		 }
		 System.gc();
		 log.debug("计算结束");
	}
   
	@Scheduled(cron="0 */1 05-11,13-15 ? * MON-FRI")
	public void grabMinuteQutoes(){
		if(!QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString())){
			if(QutoesContants.SWTICH){
				log.debug("计算分钟行情");
				GrabDataDao dao = (GrabDataDao) SpringUtil.getBean("grabDataDao");
				dao.copyRealQutoesToMinqutoes();
			}
		}
	}
	@Scheduled(cron="00 30 15 ? * MON-FRI")
	public void grabCloseQutoes(){
		if(!QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString())){
		  log.debug("计算收盘行情开始");
		   try {
			   GrabDataDao dao = (GrabDataDao) SpringUtil.getBean("grabDataDao");
			   dao.persisCloseQutoes("");
			} catch (Exception e) {
				e.printStackTrace();
			} 
		  log.debug("计算收盘行情结束");
		}
	}
	/**
	 * 仅计算实时行情，不进行转发，单条执行
	 */
	public void minQutoesThreadOneByOne(){
	int count = 0;
	long s = System.currentTimeMillis();
		try{
			int pageSize = Integer.valueOf(PropertyUtil.get("drawBatchNum"));
			try{
				String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
				if(Contants.STOCKLIST==null||Contants.STOCKLIST.size()==0){return ;}
				long total = Contants.STOCKLIST.size();
				if(Contants.STOCKLIST!=null&&total>0){
					StringBuffer codes = null;
					Pagination page= new Pagination(1,pageSize,total);
					int pageCount = page.getPageCount();
					for(int j=1;j<=pageCount;j++){//分页处理
						codes = new StringBuffer("");
						Pagination page_= new Pagination(j,pageSize,total);
						long max = page_.getPageSize()*j-1;
						if(max >=total){
							max = total-1;
						}
						log.debug("正在计算第"+page_.getCurrentPage()+"页,start="+page_.getOffset()+",end="+max);
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
					            	String exchange = codeInfo.substring(10,12);
					            	final String code = codeInfo.substring(10,codeInfo.length());
					            	final String qutoesInfo = qutoes.split("=")[1];
					            	int len = qutoes.split("=")[1].split(",").length;
					            	log.debug("[normal]"+code+","+qutoesInfo);
					            	if(len>10){
					            		count++;
					            		RealQutoes realQutoes =null;
					            		if("hk".equals(exchange)){
					            			realQutoes = RealQutoesMapper.String2ObjectHK(code,qutoesInfo);
					            		}else{
					            			realQutoes = RealQutoesMapper.String2Object(code,qutoesInfo);
					            		}
										if(!QutoesContants.realQutoesMap.containsKey(code)){
											String sql = grabDataDao.saveRealQutoes(realQutoes);
											String delSql = "delete from REALTIMEQUTOES  where code_='"+code+"'";
											grabDataDao.exeSql(delSql);
											grabDataDao.exeSql(sql);
											QutoesContants.realQutoesMap.put(code, realQutoes);
										}else{
											String sql = grabDataDao.updateRealQutoes(realQutoes);
											grabDataDao.exeSql(sql);
										}
					               }else{
					            	   log.debug("[error]"+qutoesInfo);
					               }
					            }catch(Exception e){
					            	e.printStackTrace();
					            	log.info("行情["+qutoes+"]解析异常，可忽略！"+e.getMessage());
					            }
			            } //end while
			            is.close();
			            is = null;
			            codes.delete(0, codes.length());
			          // }
					}//end 分页
				}//end not null if				
			}catch(Exception e){
				log.info(e.toString());
				e.printStackTrace();				
			}
			long e = System.currentTimeMillis();
			if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
				log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒,共耗时="+((e-s)/1000)+"."+(e-s)%1000+"秒.毫秒");
			}
		}catch(Exception e){
			log.info(e.getMessage());
		}
	}
	
	/**
	 * 仅计算实时行情，不进行转发,批量
	 */
	public void minQutoesThreadBatch1(){
		long maxSqls = Long.valueOf(PropertyUtil.get("maxSqls"));
		int count = 0;
		List<String> sqls = new ArrayList<String>();
		long s = System.currentTimeMillis();
		try{
			int pageSize = Integer.valueOf(PropertyUtil.get("drawBatchNum"));
			try{
				String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
				if(Contants.STOCKLIST==null||Contants.STOCKLIST.size()==0){return ;}
				long total = Contants.STOCKLIST.size();
				if(Contants.STOCKLIST!=null&&total>0){
					StringBuffer codes = null;
					Pagination page= new Pagination(1,pageSize,total);
					int pageCount = page.getPageCount();
					for(int j=1;j<=pageCount;j++){//分页处理
						codes = new StringBuffer("");
						Pagination page_= new Pagination(j,pageSize,total);
						long max = page_.getPageSize()*j-1;
						if(max >=total){
							max = total-1;
						}
						log.debug("正在计算第"+page_.getCurrentPage()+"页,start="+page_.getOffset()+",end="+max);
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
					            	String exchange = codeInfo.substring(10,12);
					            	final String code = codeInfo.substring(10,codeInfo.length());
					            	final String qutoesInfo = qutoes.split("=")[1];
					            	int len = qutoes.split("=")[1].split(",").length;
					            	log.debug("[normal]"+code+","+qutoesInfo);
					            	if(len>10){
					            		count++;
					            		RealQutoes realQutoes =null;
					            		if("hk".equals(exchange)){
					            			realQutoes = RealQutoesMapper.String2ObjectHK(code,qutoesInfo);
					            		}else{
					            			realQutoes = RealQutoesMapper.String2Object(code,qutoesInfo);
					            		}
										if(!QutoesContants.realQutoesMap.containsKey(code)){
											String sql = grabDataDao.saveRealQutoes(realQutoes);
											String delSql = "delete from REALTIMEQUTOES  where code_='"+code+"'";
											sqls.add(delSql);
											sqls.add(sql);
											QutoesContants.realQutoesMap.put(code, realQutoes);
										}else{
											String sql = grabDataDao.updateRealQutoes(realQutoes);
											sqls.add(sql);
										}
										if(sqls.size()>=maxSqls){
											log.debug("["+maxSqls+"]条"+"保存中...");
											grabDataDao.exeSqls(sqls);
											sqls.clear();
											sqls = new ArrayList<String>();
											log.debug("["+maxSqls+"]条"+"保存结束...");
										}
					               }else{
					            	   log.debug("[error]"+qutoesInfo);
					               }
					            }catch(Exception e){
					            	e.printStackTrace();
					            	log.info("行情["+qutoes+"]解析异常，可忽略！"+e.getMessage());
					            }
			            } //end while
			            is.close();
			            is = null;
			            codes.delete(0, codes.length());
			          // }
					}//end 分页
				}//end not null if
				if(sqls.size()>0){
					long s5 = System.currentTimeMillis();
					grabDataDao.exeSqls(sqls);
					sqls.clear();
					sqls = null;
					long e5 = System.currentTimeMillis();
					if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
						log.debug("##批量执行sql结束!##"+(e5-s5)/1000+"s."+((e5-s5)%1000)+"ms");
					}
				}
			}catch(Exception e){
				log.info(e.toString());
				e.printStackTrace();				
			}
			long e = System.currentTimeMillis();
			if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
				log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒,共耗时="+((e-s)/1000)+"."+(e-s)%1000+"秒.毫秒");
			}
		}catch(Exception e){
			log.info(e.getMessage());
		}
	}
	/**
	 * 仅计算实时行情，不进行转发,批量
	 */
	public void minQutoesThreadBatch2(){
		long maxSqls = Long.valueOf(PropertyUtil.get("maxSqls"));
		int count = 0;
		List<RealQutoes> sqlUpdate = new ArrayList<RealQutoes>();
		long s = System.currentTimeMillis();
		try{
			int pageSize = Integer.valueOf(PropertyUtil.get("drawBatchNum"));
			try{
				String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
				if(Contants.STOCKLIST==null||Contants.STOCKLIST.size()==0){return ;}
				long total = Contants.STOCKLIST.size();
				if(Contants.STOCKLIST!=null&&total>0){
					StringBuffer codes = null;
					Pagination page= new Pagination(1,pageSize,total);
					Pagination page_= null;
					int pageCount = page.getPageCount();
					for(int j=1;j<=pageCount;j++){//分页处理
						codes = new StringBuffer("");
						page_ = new Pagination(j,pageSize,total);
						long max = page_.getPageSize()*j-1;
						if(max >=total){
							max = total-1;
						}
						log.debug("正在计算第"+page_.getCurrentPage()+"页,start="+page_.getOffset()+",end="+max);
						for(int i=page_.getOffset();i<max;i++){
						   codes.append(Contants.STOCKLIST.get(i).get("CODE").toString()+",");
						}
						String url = qutoesUrl+codes.toString();
						InputStream is = UrlUtils.getStream(url);
						BufferedReader buffer = new BufferedReader(new InputStreamReader(is));
			            String qutoes = ""; 
			            String codeInfo ="";
			            RealQutoes realQutoes =null;
			            while((qutoes=buffer.readLine())!=null){			            	
				            	try{
					            	qutoes = qutoes.replaceAll(" ", "");
					            	codeInfo = qutoes.split("=")[0];
					            	String exchange = codeInfo.substring(10,12);
					            	final String code = codeInfo.substring(10,codeInfo.length());
					            	final String qutoesInfo = qutoes.split("=")[1];
					            	int len = qutoes.split("=")[1].split(",").length;
					            	log.debug("[normal]"+code+","+qutoesInfo);
					            	if(len>10){
					            		count++;
					            		if("hk".equals(exchange)){
					            			realQutoes = RealQutoesMapper.String2ObjectHK(code,qutoesInfo);
					            		}else{
					            			realQutoes = RealQutoesMapper.String2Object(code,qutoesInfo);
					            		}
										if(!QutoesContants.realQutoesMap.containsKey(code)){
											String sql = grabDataDao.saveRealQutoes(realQutoes);
											String delSql = "delete from REALTIMEQUTOES  where code_='"+code+"'";
											grabDataDao.exeSql(delSql);
											grabDataDao.exeSql(sql);
											QutoesContants.realQutoesMap.put(code, realQutoes);
										}else{
											sqlUpdate.add(realQutoes);
										}
										if(sqlUpdate.size()>=maxSqls){
											log.debug("["+maxSqls+"]条"+"保存中...");
											grabDataDao.updateRealtimeBatch(sqlUpdate);
											sqlUpdate.clear();
											sqlUpdate = new ArrayList<RealQutoes>();
											log.debug("["+maxSqls+"]条"+"保存结束...");
										}
					               }else{
					            	   log.debug("[error]"+qutoesInfo);
					               }
					            }catch(Exception e){
					            	e.printStackTrace();
					            	log.info("行情["+qutoes+"]解析异常，可忽略！"+e.getMessage());
					            }
			            } //end while
			            is.close();
			            is = null;
			            codes.delete(0, codes.length());
			          // }
					}//end 分页
				}//end not null if
				if(sqlUpdate.size()>0){
					long s5 = System.currentTimeMillis();
					grabDataDao.updateRealtimeBatch(sqlUpdate);
					sqlUpdate.clear();
					sqlUpdate = null;
					long e5 = System.currentTimeMillis();
					if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
						log.debug("##批量执行sql结束!##"+(e5-s5)/1000+"s."+((e5-s5)%1000)+"ms");
					}
				}
			}catch(Exception e){
				log.info(e.toString());
				e.printStackTrace();				
			}
			long e = System.currentTimeMillis();
			if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
				log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒,共耗时="+((e-s)/1000)+"."+(e-s)%1000+"秒.毫秒");
			}
		}catch(Exception e){
			log.info(e.getMessage());
		}
	}
	/**
	 * 计算实时行情并进行转发
	 */
	public void dispatchAndCalculate(){
		int count = 0;
		int count1 = 0;
		List<String> sqls = new ArrayList<String>();
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
								        RealQutoes realQutoes = RealQutoesMapper.String2Object(code,qutoesInfo);
										if(!QutoesContants.realQutoesMap.containsKey(code)){
											sqls.add(grabDataDao.saveRealQutoes(realQutoes));
											QutoesContants.realQutoesMap.put(code, realQutoes);
										}else{
											sqls.add(grabDataDao.updateRealQutoes(realQutoes));
										}
										if(count1>=2000){
											long s5 = System.currentTimeMillis();
										    count1=0;
											grabDataDao.exeSqls(sqls);
											sqls.clear();
											sqls = new ArrayList<String>();
											long e5 = System.currentTimeMillis();
											log.info("*批量执行sql结束！*"+(e5-s5)/1000+"s."+((e5-s5)%1000)+"ms");
										}
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
						}//end 分页
					}//end not null if
					if(sqls.size()>0){
						long s5 = System.currentTimeMillis();
						grabDataDao.exeSqls(sqls);
						sqls.clear();
						sqls = null;
						long e5 = System.currentTimeMillis();
						log.info("##批量执行sql结束!##"+(e5-s5)/1000+"s."+((e5-s5)%1000)+"ms");
					}
				}catch(Exception e){
					log.info(e.toString());
				}
				long e = System.currentTimeMillis();
				if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
					log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒,共耗时="+((e-s)/1000)+"."+(e-s)%1000+"秒.毫秒");
				}
	  }catch(Exception e){}
	}
	/**
	 * 只进行转发，不进行计算
	 */
	public void dispatchNoCaculate(){
	    long s = System.currentTimeMillis();
	    String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
		String code = "";
		String exchange = "";
		String retData = "";
		int count = 0 ;
		long total = Contants.STOCKLIST.size();
			if(Contants.STOCKLIST!=null&&total>0){
			 for(int idx=0;idx<total;idx++){
				Map<String,Object> stock = Contants.STOCKLIST.get(idx);
			   try{
					code = stock.get("CODE").toString();				
					retData = GrabEntity.grabJsonDataFromURL(qutoesUrl+code);				
					retData = retData.replace("\"", "");
					String dataStr = retData.split("=")[1];
					if(!";".equals(dataStr)){
						final Map<String,String> data = new HashMap<String,String>();
						data.put("qutoes", dataStr);
						data.put("code", code);
						MessageCreator messageCreator = new MessageCreator() {   
				            public Message createMessage(Session session) throws JMSException { 
				            	 MapMessage message=session.createMapMessage();  
								 message.setString("qutoes", data.get("qutoes"));
								 message.setString("code", data.get("code"));
								 message.setString("sendTime", DateUtil.timeToMilis(new Date()));
								 if(data.get("code").equals("sh600868")){
									log.info("send:"+message.getString("qutoes"));
								 }
				                return message;   
				            }   
				     };
				     jmsTemplate.send(destination, messageCreator);
				     count++;
				  }//end if
				}catch(Exception e){
					log.info(qutoesUrl+exchange+code+"，数据抓取失败！"+e.toString());
				}
			  }//end for
		  }
	     long e = System.currentTimeMillis();
	     if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
				log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒,共耗时="+((e-s)/1000)+"."+(e-s)%1000+"秒.毫秒");
		  }
	}
	/**
	 * 只进行转发，不进行计算
	 */
	public void dispatchNoCaculateBatch(){
		  long s = System.currentTimeMillis();
		  int count =0;
		  String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
			String code = "";
			String exchange = "";
			String retData = "";
			long total = Contants.STOCKLIST.size();
				if(Contants.STOCKLIST!=null&&total>0){
				 List<MessageCreator> lst = new ArrayList<MessageCreator>();
				 for(int idx=0;idx<total;idx++){
					Map<String,Object> stock = Contants.STOCKLIST.get(idx);
				   try{
					    long s1 = System.currentTimeMillis();
						code = stock.get("CODE").toString();				
						retData = GrabEntity.grabJsonDataFromURL(qutoesUrl+code);				
						retData = retData.replace("\"", "");
						String dataStr = retData.split("=")[1];
						if(!";".equals(dataStr)){
							count++;
							final Map<String,String> data = new HashMap<String,String>();
							data.put("qutoes", dataStr);
							data.put("code", code);
							MessageCreator messageCreator = new MessageCreator() {   
					            public Message createMessage(Session session) throws JMSException { 
					            	 MapMessage message=session.createMapMessage();  
									 message.setString("qutoes", data.get("qutoes"));
									 message.setString("code", data.get("code"));
									 message.setString("sendTime", DateUtil.timeToMilis(new Date()));
									 if(data.get("code").equals("sh600868")){
										log.info("send:"+message.getString("qutoes"));
									 }
					                return message;   
					            }   
					     };
					     lst.add(messageCreator);
						 if(lst.size()>0&&(lst.size()==1000)){
							 long s2 = System.currentTimeMillis();
							 log.info("批量发送"+lst.size()+"...");
							 for(MessageCreator mess:lst){
								 jmsTemplate.send(destination,mess);  
							 }
							 lst.clear();
							 lst = new ArrayList<MessageCreator>();
							 long e2 = System.currentTimeMillis();
							 log.info("批量发送 spend time is "+(e2-s2)/1000+"s."+(e2-s2)%1000+"ms");
					     }
						 long e1 = System.currentTimeMillis();
					     log.info("("+idx+"/"+total+")per record spend time is "+(e1-s1)/1000+"s."+(e1-s1)%1000+"ms");
					  }//end if
					}catch(Exception e){
						log.info(qutoesUrl+exchange+code+"，数据抓取失败！"+e.toString());
					}
				  }//end for
				 if(lst.size()>0){
					 for(MessageCreator mess:lst){
						 jmsTemplate.send(destination,mess);  
					 }
				 }
			  }
		     long e = System.currentTimeMillis();
		     if(Boolean.valueOf(PropertyUtil.get("showrapid"))){
					log.info("处理记录数="+count+",数据处理速度="+count/(e-s)+"条/毫秒,共耗时="+((e-s)/1000)+"."+(e-s)%1000+"秒.毫秒");
			 }
	}
	
	class MinQutoesThread1 implements Runnable{
		protected Log  log1 = LogFactory.getLog(MinQutoesThread1.class);
		public void run() {} 
	   }
	
	class SubmitMessageTask1 implements Runnable{
		protected Log  log1 = LogFactory.getLog(MinQutoesThread1.class);
		List<MessageCreator> lst3 = null;
		MessageCreator msg = null;
		public  SubmitMessageTask1( List<MessageCreator> lst){
			this.lst3 = lst;
		}
		@Override
		public synchronized void run() {
}		
	}
	@Scheduled(cron="00 28 09 ? * MON-FRI")
	public void openNoon(){
		if(!QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString())){
			log.info("行情开关打开...");
			QutoesContants.SWTICH = true;
			this.jdbcTemplate.execute("update  STOCKCONFIG set curjyr ="+DateUtil.date2long8(new Date()).toString()+" where alias='LASTESTDAY'");
			log.info("更新交易日成功！");
			this.jdbcTemplate.execute("delete from MINUTEQUTOESCURRDAY ");
			this.jdbcTemplate.execute("delete from REALTIMEQUTOES  ");
			QutoesContants.realQutoesMap.clear();
			QutoesContants.realQutoesMap = new HashMap<String,RealQutoes>();
			log.info("清除上一个交易日分钟行情数据成功！");
		}
	}
	@Scheduled(cron="00 31 11 ? * MON-FRI")
	public void closeNoon(){
		if(!QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString())){
			log.info("行情开关被关闭！");
			QutoesContants.SWTICH = false;
		}
	}
	@Scheduled(cron="00 58 12 ? * MON-FRI")
	public void openAfterNoon(){
		if(!QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString())){
			log.info("行情开关被打开！");
			QutoesContants.SWTICH = true;
		}
	}
	@Scheduled(cron="00 01 15 ? * MON-FRI")
	public void closeAfterNoon(){
		if(!QutoesContants.holidays.containsKey(DateUtil.date2long8(new Date()).toString())){
		  log.info("行情开关被关闭！");
		  QutoesContants.SWTICH = false;
		}
	}
	public static void main(String[] args) {
		String qutoes = "var hq_str_sh600868=梅雁吉祥,5.100,5.150".replaceAll(" ", "");
		String codeInfo = qutoes.split("=")[0];
    	String qutoesInfo = qutoes.replaceAll(" ", "").split("=")[1];
    	System.out.println(codeInfo.substring(10,codeInfo.length()));
    	System.out.println(qutoesInfo);
    	Pagination page= new Pagination(1,300,2756);
		int pageCount = page.getPageCount();
		for(int j=1;j<=pageCount;j++){//分页处理
			Pagination page_= new Pagination(j,300,2756);
			long max = page_.getPageSize()*j-1;
			if(max >=2765){ max = 2765;}
			System.out.println(page_.getOffset()+","+max);
			/*for(int i=page_.getOffset();i<=page.getPageSize()*j;i++){
			  
			}*/
		}
		System.out.println(";".split(","));
	}
	public synchronized Map<String,Object> getDataSource(){
		if(!dataSource.containsKey("con")){
			try {
				dataSource.put("con", jdbcTemplate.getDataSource().getConnection());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		if(!dataSource.containsKey("stmt")){
			try {
				dataSource.put("stmt", jdbcTemplate.getDataSource().getConnection().createStatement());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return dataSource;
	}
}
