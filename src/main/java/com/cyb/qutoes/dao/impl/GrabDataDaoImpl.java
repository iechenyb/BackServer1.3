package com.cyb.qutoes.dao.impl;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;
import javax.jms.MapMessage;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import com.cyb.jms.ActiveMqManager;
import com.cyb.qutoes.bean.KPoint;
import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.EContants;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.mapper.RealQutoesMapper;
import com.cyb.qutoes.utils.GrabEntity;
import com.cyb.qutoes.utils.StockCodeInitEntity;
import com.cyb.utils.Contants;
import com.cyb.utils.DataUtils;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.QutoesUtils;
import com.cyb.utils.UUIDUtils;
@Repository("grabDataDao")
public class GrabDataDaoImpl implements GrabDataDao {
	Log log = LogFactory.getLog(GrabDataDaoImpl.class);
	@Resource
	JdbcTemplate jdbcTemplate; 
	public synchronized int query(String code,String jys){
		String sql = "SELECT count(*) as num FROM STOCK where code_='"+code+"' and exchange_='"+jys+"'";
	    List<Map<String, Object>> list = this.jdbcTemplate.queryForList(sql);
	    System.out.println("count="+list.get(0).get("num").toString()+","+sql);
	    String  num = list.get(0).get("num").toString();
	    if("0".equals(num)){
	    	return 0;
	    }else{
	    	return list.size();
	    }
	}
	public synchronized void insertStock(String code,String jys,String name){
		/*String sql = "SELECT count(*) FROM STOCK where code_='"+code+"' and exchange_='"+jys+"'";
	    List<?> list = this.jdbcTemplate.queryForList(sql);
	    System.out.println("count="+list.size()+","+sql);*/
	    /*if(CollectionUtils.isEmpty(list)){ }*/
	    String sql1 = "insert into stock(id_,code_,exchange_,name_,industry,classify,oper_time) values ('"+UUIDUtils.getUUID()+"','"+code+"','"+jys+"','"+name+"','','',sysdate)";
	    System.out.println("insert:"+sql1);
		this.exeSql(sql1);
	}
	public void saveCodeInfor() {
		BufferedReader reader = null;
		try {
			String savePath = Contants.WEBPATH+PropertyUtil.getValueByKey("App", "stockFile")+File.separator+PropertyUtil.getValueByKey("App", "codeFileName"); 
			File codeFile = new File(savePath);
			if(!codeFile.exists()){
				GrabEntity.downLoadFromUrl(PropertyUtil.getValueByKey("App", "cfcenter"),savePath);
			}
			reader = new BufferedReader(new InputStreamReader(new FileInputStream(codeFile),"GBK"));
			String content = "";
			List<String> sqls = new ArrayList<String>();
			Map<String,String> infor= null;
			int sqlsize = 0;
			while((content = reader.readLine())!=null){
				try{
					if(content.contains("<li><a")){
						sqlsize ++;
						infor = GrabEntity.getCodeInfor(content.replace("\"", ""));
						if(infor.get("code")!=null||!"null".equals(infor.get("code"))||!"".equals(infor.get("code"))){
							String sql = "SELECT count(*) FROM STOCK where code_='"+infor.get("code")+"' and exchange_='"+infor.get("exchange")+"'";
							int count = jdbcTemplate.queryForInt(sql);
							if(count==0){
								sqls.add("insert into stock(id_,code_,exchange_,name_,industry,classify,oper_time) values ('"+UUIDUtils.getUUID()+"','"+infor.get("code")+"','"+infor.get("exchange")+"','"+infor.get("name")+"','fund','fund',sysdate)");
							}
							if(sqlsize>2000){
								sqlsize=0;
								this.jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
								sqls.clear();
								sqls = new ArrayList<String>();
							}
						}
					}
				}catch (Exception e) {}
			}
			this.jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
			this.jdbcTemplate.execute("delete from stock  where code_='null' or code_ is null ");
			log.info("持久化code成功！");
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		} finally{
			if(reader !=null){
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				reader = null; 
			}
		}
	}
	//从交易所网站获取完整的代码
	public void persisCodeInforFromExchange() {
		StockCodeInitEntity entity = new StockCodeInitEntity();
		entity.initIndustry();
		String fileName = "";
		String root = PropertyUtil.getValueByKey("App", "stockFile");
		String shFile = Contants.WEBPATH+root+File.separator+"sh"+File.separator+"-";
		String szFile = Contants.WEBPATH+root+File.separator+"sz"+File.separator;
		for(int i=0;i<QutoesContants.INDUSTRYSORT.size();i++){
			  String industry = QutoesContants.INDUSTRYSORT.get(i).get("type");
			  fileName=shFile+QutoesContants.INDUSTRYSORT.get(i).get("type")+".xls";
			  entity.initSHStocks(fileName,industry);
		  }
		//持久化上海证券交易所的所有股票
		
		//持久化深圳证券交易所所有股票
	}
	public List getAllCodeInfor() {
		List list = null;
		try {
			 list = this.jdbcTemplate.queryForList("select * from stock ");
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		return list;
	}
	
	public void persisMinQutoes(String stockCode){
		Long currDate = DateUtil.date2long8(new Date());
		String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
		List<Map<String, Object>> stocks = this.jdbcTemplate.queryForList("select * from stock ");
		String code = "";
		String exchange = "";
		String retData = "";
		StringBuffer values = new StringBuffer("");
		String insert = "insert into MINUTEQUTOES (id_,record_date_,name_,code_,open_,preclose_,high_,low_,price_,close_,day_,time_,oper_time) values " ;
		List<String> sqls = new ArrayList<String>();
		try {
			exchange = Contants.STOCKMAP.get(stockCode).toString();
			retData = GrabEntity.grabJsonDataFromURL(qutoesUrl+exchange+stockCode);
			retData = retData.replace("\"", "");
			String dataStr = retData.split("=")[1];
			if(!";".equals(dataStr)){
				String[] dataArr = dataStr.split(",");
				values.append("(");
				values.append("'"+UUIDUtils.getUUID()+"' ,");
				values.append(""+currDate+",");
				values.append("'"+dataArr[Contants.NAME]+"' ,");
				values.append("'"+exchange+stockCode+"' ,");
				values.append(""+dataArr[Contants.OPEN]+" ,");
				values.append(""+dataArr[Contants.PRECLOSE]+" ,");
				values.append(""+dataArr[Contants.HIGH]+" ,");
				values.append(""+dataArr[Contants.LOW]+" ,");
				values.append(""+dataArr[Contants.PRICE]+" ,");
				values.append(""+dataArr[Contants.PRICE]+" ,");
				values.append("'"+dataArr[Contants.DAY]+"' ,");
				values.append("'"+dataArr[Contants.TIME]+"' ,");
				values.append("sysdate");
				values.append(")");
				log.info("保存分钟行情："+insert+values);
				this.jdbcTemplate.execute(insert+values);
			}
			values = new StringBuffer("");
		} catch (Exception e) {
			log.info("出现异常！"+qutoesUrl+exchange+code);
		}
	}
	public void persisMinQutoesBatch(List<Map<String, Object>> stocks){
		ExecutorService cachedThreadPool = Executors.newCachedThreadPool();
		String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
		String code = "";
		String exchange = "";
		String retData = "";
		if(ActiveMqManager.producer==null){
			ActiveMqManager.createProducer();
		}
		if(stocks!=null&&stocks.size()>0){
		 for(Map<String,Object> stock :stocks){
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
					if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
						if(data.get("code").equals(PropertyUtil.get("testcode"))){
							log.info("send:"+message.getString("qutoes"));
						}
					}
					ActiveMqManager.producer.send(message);
					ActiveMqManager.session.commit();
				}
			}catch(Exception e){
				log.info(qutoesUrl+exchange+code+"，数据抓取失败！"+e.toString());
			}
		  }
		}
	}
	public void persisCloseQutoes(String stockCode){
		log.info("收盘行情开始处理...");
		Long currDate = DateUtil.date2long8(new Date());
		String qutoesUrl = PropertyUtil.getValueByKey("App", "qutoesURL");
		String code = "";
		String exchange = "";
		String retData = "";
		StringBuffer values = new StringBuffer("");
		this.jdbcTemplate.execute("delete from CLOSEQUTOES where record_date_ = "+currDate);
		String insert = "insert into CLOSEQUTOES (id_,record_date_,name_,code_,open_,preclose_,high_,low_,price_,close_,day_,time_,oper_time) values " ;
		List<String> sqls = new ArrayList<String>();
		try {
			 int i=0;
			 for(Map stock :Contants.STOCKLIST){
				 try{
					code = stock.get("CODE").toString();
					retData = GrabEntity.grabJsonDataFromURL(qutoesUrl+code);
					retData = retData.replace("\"", "");
					String dataStr = retData.split("=")[1];
					if(!";".equals(dataStr)){
						String[] dataArr = dataStr.split(",");
						values.append("(");
						values.append("'"+UUIDUtils.getUUID()+"' ,");
						values.append(""+currDate+",");
						values.append("'"+dataArr[Contants.NAME]+"' ,");
						values.append("'"+code+"' ,");
						values.append(""+dataArr[Contants.OPEN]+" ,");
						values.append(""+dataArr[Contants.PRECLOSE]+" ,");
						values.append(""+dataArr[Contants.HIGH]+" ,");
						values.append(""+dataArr[Contants.LOW]+" ,");
						values.append(""+dataArr[Contants.PRICE]+" ,");
						values.append(""+dataArr[Contants.PRICE]+" ,");
						values.append("'"+dataArr[Contants.DAY]+"' ,");
						values.append("'"+dataArr[Contants.TIME]+"' ,");
						values.append("sysdate");
						values.append(")");
						sqls.add(insert+values);
						i++;
						log.debug("["+i+"]"+insert+values);
					}
					values = new StringBuffer("");
				} catch (Exception e) {
					values = new StringBuffer("");
					log.info(""+qutoesUrl+exchange+code+":"+e.toString());
					e.printStackTrace();
			    }
			  }//end for
			 if(sqls.size()>0){
				 this.jdbcTemplate.batchUpdate(sqls.toArray(new String[sqls.size()]));
			 }
			 log.info("存储收盘行情结束~");
		} catch (Exception e) {
			log.info(""+qutoesUrl+exchange+code);
		}
	}

	public List getAllDayQutoes(String stockCode) {
		return null;
	}

	public void test(String stockCode) {
		
	}
	//持久化大盘指数
	public  void persisCloseQutoesIndicator(String code) {
		Long currDate = DateUtil.date2long8(new Date());
		String urlStr = "http://hq.sinajs.cn/list=s_sh000001";
		String  content = GrabEntity.grabJsonDataFromURL(urlStr);
		String inserts = "insert into grailqutoes(id_,record_date_,name_,code_,open_,high_,low_,close_,price_,oper_time) values ";
		String values = "";
		if(content!=null&&!"".equals(content)){
			   String[] arr = content.split("=")[1].replace("\"", "").split(",");
			   String name = arr[Contants.GRAILNAME];
			   String point = arr[Contants.GRAILPOINT];
			   String number = arr[Contants.GRAILTRADENUM];
			   String money = arr[Contants.GRAILTRADEMONEY];
			   values = "('"+UUIDUtils.getUUID()+"',"+currDate+",'"+name+"','sh000001',0,0,0,0,"+point+",sysdate)";
			   log.info(inserts+values);
		}
		this.jdbcTemplate.execute(inserts+values);
	}
	public List<Map<String, Object>> staticsCompany(){
		 List<Map<String, Object>> retdata = new ArrayList<Map<String,Object>>();
		 try {
			String sql = "SELECT count(*) num,zdbz FROM realtimequtoes where open_>0 group by ZDBZ order by zdbz asc";
			 List<Map<String, Object>> data = this.jdbcTemplate.queryForList(sql);
			 Map<String, Object>  data_  = null;
			
			 if(data!=null&&data.size()>0){
				 for(Map tmp :data){
					 data_ = new LinkedHashMap<String, Object>();
					 data_.put("name", EContants.zdbzMap.get(tmp.get("ZDBZ").toString()));
					 data_.put("value", tmp.get("num").toString());
					 data_.put("type", EContants.zdbzMapParam.get(tmp.get("ZDBZ").toString()));
					 retdata.add(data_);
				 }
			 }
		} catch (DataAccessException e) {
			e.printStackTrace();
		}
		 return retdata;
	  }
	public Map<String,Object> hsindiator(){
		  Map<String,Object> map = new LinkedHashMap<String,Object>();
		  String sql_zs =" select (price_-preCLOSE_ ) A1,(price_-preCLOSE_ )/preclose_*100  as A,price_ as price,code_,name_ "
		  		+ " from REALTIMEQUTOES  where code_ in('sz399001','sh000001')";
		    List<Map<String,Object>> list_zhzs = this.jdbcTemplate.queryForList(sql_zs);
		    String shzsprice = "0";
		    String szzsprice = "0";
		    String shzsA = "0";
		    String szzsA = "0";
		    String shzsA1 = "0";
		    String szzsA1 = "0";
		    String colorsh="green";
		    String colorsz="green";
		    if(list_zhzs!=null&&list_zhzs.size()>0){
		    	for(Map zhzs :list_zhzs){
		    		String code_ = zhzs.get("CODE_").toString();
		    		if("sz399001".equals(code_)){
		    			szzsprice =zhzs.get("PRICE").toString();
		    			szzsA = DataUtils.roundStr(Double.valueOf(zhzs.get("A").toString()), 2);
		    			szzsA1 = DataUtils.roundStr(Double.valueOf(zhzs.get("A1").toString()), 2);
		    			if(Double.valueOf(szzsA)>=0){
		    				szzsA="+"+szzsA;
		    				szzsA1="+"+szzsA1;
		    				colorsz="red";
		    			}else if( Double.valueOf(szzsA)==0){
		    				szzsA="+"+szzsA;
		    				szzsA1="+"+szzsA1;
		    				colorsz="gray";
		    			}else if( Double.valueOf(szzsA)<0){
		    				//szzsA="-"+szzsA;
		    				colorsz="green";
		    			}
		    		}else if("sh000001".equals(code_)){
		    			shzsprice =zhzs.get("PRICE").toString();
		    			shzsA = DataUtils.roundStr(Double.valueOf(zhzs.get("A").toString()), 2);
		    			shzsA1 = DataUtils.roundStr(Double.valueOf(zhzs.get("A1").toString()), 2);
		    			if(Double.valueOf(shzsA)>=0){
		    				shzsA="+"+shzsA;
		    				shzsA1="+"+shzsA1;
		    				colorsh="red";
		    			}else if(Double.valueOf(shzsA)==0){
		    				shzsA="+"+shzsA;
		    				shzsA1="+"+shzsA1;
		    				colorsh="gray";
		    			}else if(Double.valueOf(shzsA)<0){
		    				//shzsA="-"+shzsA;
		    				colorsh="green";
		    			}
		    		}
		    	}
		    }
		    map.put("shzsprice", shzsprice);
		    map.put("szzsprice", szzsprice);
		    map.put("shzsA", shzsA);
		    map.put("szzsA", szzsA);
		    map.put("shzsA1", shzsA1);
		    map.put("szzsA1", szzsA1);
		    map.put("colorsh", colorsh);
		    map.put("colorsz", colorsz);
		    return map;
	  }
	public Map<String,Object> lineJson(String code){
	  	Map<String,Object> map = new HashMap<String,Object>();
		//String jyrsql = "(select  curjyr from STOCKCONFIG where alias='LASTESTDAY' )";
	  	if(code!=null&&!"".equals(code)){
	  		String sql_zs =" select (price_-preCLOSE_ ) A1,(price_-preCLOSE_ )/preclose_*100  as A,price_ as price,code_,name_ from REALTIMEQUTOES  where  code_ in('sz399001','sh000001')";
		    List<Map<String,Object>> list_zhzs = this.jdbcTemplate.queryForList(sql_zs);
		    String shzsprice = "0";
		    String szzsprice = "0";
		    String shzsA = "0";
		    String szzsA = "0";
		    String shzsA1 = "0";
		    String szzsA1 = "0";
		    String colorsh="green";
		    String colorsz="green";
		    if(list_zhzs!=null&&list_zhzs.size()>0){
		    	for(Map<String,Object> zhzs :list_zhzs){
		    		String code_ = zhzs.get("CODE_").toString();
		    		if("sz399001".equals(code_)){
		    			szzsprice =zhzs.get("PRICE").toString();
		    			szzsA = DataUtils.roundStr(Double.valueOf(zhzs.get("A").toString()), 2);
		    			szzsA1 = DataUtils.roundStr(Double.valueOf(zhzs.get("A1").toString()), 2);
		    			if(Double.valueOf(szzsA)>0){
		    				szzsA="+"+szzsA;
		    				szzsA1="+"+szzsA1;
		    				colorsz="red";
		    			}else{
		    				/*szzsA="-"+szzsA;
		    				szzsA1="-"+szzsA1;*/
		    			}
		    		}else if("sh000001".equals(code_)){
		    			shzsprice =zhzs.get("PRICE").toString();
		    			shzsA = DataUtils.roundStr(Double.valueOf(zhzs.get("A").toString()), 2);
		    			shzsA1 = DataUtils.roundStr(Double.valueOf(zhzs.get("A1").toString()), 2);
		    			if(Double.valueOf(shzsA)>0){
		    				shzsA="+"+shzsA;
		    				shzsA1="+"+shzsA1;
		    				colorsh="red";
		    			}else{
		    				/*shzsA1="-"+shzsA1;
		    				shzsA="-"+shzsA;*/
		    			}
		    		}
		    	}
		    }
		    
		    
		    	String sql = "SELECT substr(oper_time,0,16)  as time_ ,price_,nvl(PRECLOSE_,0) as PRECLOSE_,HIGH_,LOW_,OPEN_,time_min FROM MINUTEQUTOESCURRDAY where code_='"+code.trim()+"' order by time_ asc";
		        List<Map<String,Object>> list = this.jdbcTemplate.queryForList(sql);
		        List<String> x = new ArrayList<String>();
		        List<String> y = new ArrayList<String>();
		        Map<String, String> xMap = QutoesUtils.spiltTimeMap("", 1);//获取所有的坐标
		        double max = 0.0;double min = 0.0;
		        double preclose=0.0;double curPrice=0.0;
		        double open = 0.0;double cje=0.0;double cjl=0.0;
		        if(list!=null&&list.size()>0){
		        	for(Map<String,Object> tmp:list){
		        		curPrice = Double.valueOf(tmp.get("PRICE_").toString());
		        		String time_ = tmp.get("TIME_MIN").toString();
		        		String res = DateUtil.format(time_+"00", "yyyy-MM-dd HH:mm:ss");
		        		String xVal = res.substring(11, res.length()-3);
		        		x.add(res.substring(11, res.length()-3));
		        		if(xMap.containsKey(xVal)){
		        			xMap.put(xVal, curPrice+"");
		        		}
		        		y.add(curPrice+"");
		        	}
		        	open = Double.valueOf(list.get(list.size()-1).get("OPEN_").toString());
		        	preclose = Double.valueOf(list.get(list.size()-1).get("PRECLOSE_").toString());
		        	String newSql = "SELECT price_,high_,low_,VOLUME,TURNVOLUME FROM REALTIMEQUTOES where code_='"+code.trim()+"'";
		        	List<Map<String,Object>> listNew = this.jdbcTemplate.queryForList(newSql);
		        	max = Double.valueOf(listNew.get(0).get("HIGH_").toString());
		        	min = Double.valueOf(listNew.get(0).get("LOW_").toString());
		        	cje = Double.valueOf(listNew.get(0).get("VOLUME").toString());
		        	cjl = Double.valueOf(listNew.get(0).get("TURNVOLUME").toString());
		        	if(max==0||min==0){
		        		max = Double.valueOf(list.get(list.size()-1).get("HIGH_").toString());
			        	min = Double.valueOf(list.get(list.size()-1).get("LOW_").toString());
		        	}
		        	curPrice =  Double.valueOf(listNew.get(0).get("PRICE_").toString());
		        	List<String> tmpy = new ArrayList<String>();
		        	tmpy.addAll(xMap.values());
		        	int maxIndex = QutoesUtils.getIndexOfNotNullValueList(tmpy);
		        	x.clear();
		        	y.clear();
		        	if(maxIndex>0){
		        		for(int i=1;i<maxIndex-1;i++){
		        			if(tmpy.get(i).equals("-")){
		        				tmpy.set(i, tmpy.get(i-1));
		        			}
		        		}
		        		x.addAll(xMap.keySet());
	        			y.addAll(tmpy);
		        	}else{
		        		x.addAll(xMap.keySet());
	        			y.addAll(xMap.values());
		        	}		        	
        			
		        	if(x!=null&&x.size()>0){
		        		map.put("x", JSONArray.fromObject(x));
		        	}else{
		        		map.put("x", JSONArray.fromObject("[]"));
		        	}
		        	//y.set(y.size()-1,String.valueOf(curPrice));
		        	y.add(String.valueOf(curPrice));
		        	if(y!=null&&y.size()>0){
		        		map.put("y", JSONArray.fromObject(y));
		        	}else{
		        		map.put("y", JSONArray.fromObject("[]"));
		        	}
		        	map.put("realmax", max);
		        	map.put("realmin", min);
		        	Map<String,Double> res = QutoesUtils.calSection(max, min, preclose);
		        	max = res.get("max");
		        	min = res.get("min");
		        }else{
		        	return new HashMap<String,Object>();
		        }
	        double amplitude  =  0;
		    if(preclose!=0){
		    	amplitude = 100*(curPrice-preclose)/preclose;  
		    }
		    
		    map.put("shzsprice", shzsprice);
		    map.put("szzsprice", szzsprice);
		    map.put("shzsA", shzsA);
		    map.put("szzsA", szzsA);
		    map.put("shzsA1", shzsA1);
		    map.put("szzsA1", szzsA1);
		    map.put("colorsh", colorsh);
		    map.put("colorsz", colorsz);
		    map.put("gap", DataUtils.roundStr(curPrice-preclose,2));
		    map.put("price", DataUtils.roundStr(curPrice,2));
		    map.put("A", DataUtils.roundStr(amplitude, 2));//涨跌幅   
		    map.put("name", Contants.STOCKMAP.get(code));  
	        map.put("max", max);
	        map.put("close", preclose);
	        map.put("min", min);
	        map.put("code", code);
	        map.put("open", open);
	        map.put("cjl", cjl);
	        map.put("cje", cje);
	        map.put("start","09:30");
	        map.put("end","15:30");
	        map.put("time", DateUtil.format(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
	  	}
	      return map;
	  }  
	
	public void copyRealQutoesToMinqutoes(){
		Long currDate = DateUtil.date2long14(new Date());
		String insert = "insert into MINUTEQUTOESCURRDAY(id_,record_date_,name_,code_,"
				+ "open_,preclose_,high_,low_,price_,close_,day_,time_,"
				+ "oper_time,time_min,industry,volume,turnvolume,zdbz) values " ;
		String sql = "select * from REALTIMEQUTOES ";
		StringBuffer values = new StringBuffer("");
		List<RealQutoes> lst = this.jdbcTemplate.query(sql, new RealQutoesMapper());
		List<String> k = null;
		if(lst!=null&&lst.size()>0){
			for(int i=0;i<lst.size();i++){
				try{
					RealQutoes vo = lst.get(i);
					k = new ArrayList<String>();
					/*double preclose = Double.valueOf(vo.getPreClose());
					double price = Double.valueOf(vo.getPrice());
					double open = Double.valueOf(vo.getOpen());
					double A = 100*(price-preclose)/preclose;
					int zdbz = 0;//平盘
					if(open>0){
						if(A>=9){
							zdbz=1;
						}else if(A>0&&A<9){//上涨
							zdbz=3;
						}
						if(A<=-9){
							zdbz=2;
						}else if(A>-9&&A<0){
							zdbz=4;//下跌
						}								
					}else if(open==0){
						zdbz=5;//停牌
						price = preclose;
					}*/
					values.append("(");
					values.append("'"+UUIDUtils.getUUID()+"' ,");
					values.append(""+currDate.toString().substring(0, 8)+",");
					values.append("'"+vo.getName()+"' ,");
					values.append("'"+vo.getCode()+"' ,");
					values.append(""+vo.getOpen()+" ,");
					values.append(""+vo.getPreClose()+" ,");
					values.append(""+vo.getHigh()+" ,");
					values.append(""+vo.getLow()+" ,");
					values.append(""+vo.getPrice()+" ,");
					values.append(""+vo.getPrice()+" ,");
					values.append("'"+vo.getDay()+"' ,");
					values.append("'"+vo.getTime()+"' ,");
					values.append("sysdate,");
					values.append(currDate.toString().substring(0, 12)+",");
					values.append("'"+vo.getIndustry()+"',");
					values.append(vo.getVolume()+" ,");
					values.append(vo.getTurnvolume()+",");
					values.append(vo.getZdbz());
					values.append(")");
					k.add(vo.getOpen());
	        		k.add(vo.getPrice());
	        		k.add(vo.getLow());
	        		k.add(vo.getHigh());
					QutoesContants.KLineOfTODAY.put(vo.getCode(), k);
					try {
						this.jdbcTemplate.execute(insert+values);
					} catch (DataAccessException e) {
						e.printStackTrace();
					}
					values = new StringBuffer("");
				}catch (Exception e) {
					log.info(e.toString());
					e.printStackTrace();
				}
			}
		}
	}
	
	public String updateRealQutoes(RealQutoes realQutoes){
		StringBuffer sql = new StringBuffer("");
		sql.append(" update REALTIMEQUTOES set ");
		sql.append(" low_ ="+realQutoes.getLow());
		sql.append(" ,high_ ="+realQutoes.getHigh());
		sql.append(" ,open_ ="+realQutoes.getOpen());
		sql.append(" ,price_ ="+realQutoes.getPrice());
		sql.append(" ,close_ ="+realQutoes.getClose());
		sql.append(" ,preclose_ ="+realQutoes.getPreClose());
		sql.append(" ,volume ="+realQutoes.getVolume());
		sql.append(" ,turnvolume ="+realQutoes.getTurnvolume());
		sql.append(" ,zdbz ="+realQutoes.getZdbz());
		sql.append(" ,day_ ='"+realQutoes.getDay()+"'");
		sql.append(" ,time_ ='"+realQutoes.getTime()+"'");
		sql.append(" ,OPER_TIME =sysdate" );
		sql.append(" where code_='"+realQutoes.getCode()+"'");// and id_='"+realQutoes.getId()+"'
		if(realQutoes.getCode().equals("sh600868")){
			log.debug(sql);
		}
		return sql.toString();
	   // this.jdbcTemplate.execute(sql.toString());
	}
	public String saveRealQutoes(RealQutoes realQutoes){
		String insert = "insert into REALTIMEQUTOES  (id_,record_date_,name_,code_,open_,preclose_,high_,low_,price_,close_,day_,time_,volume,turnvolume,zdbz,industry,oper_time) values " ;
		StringBuffer values = new StringBuffer("");
		realQutoes.setId(UUIDUtils.getUUID());
		Long currDate = DateUtil.date2long8(new Date());
		values.append("(");
		values.append("'"+realQutoes.getId()+"' ,");
		values.append(""+currDate+",");
		values.append("'"+realQutoes.getName()+"' ,");
		values.append("'"+realQutoes.getCode()+"' ,");
		values.append(""+realQutoes.getOpen()+" ,");
		values.append(""+realQutoes.getPreClose()+" ,");
		values.append(""+realQutoes.getHigh()+" ,");
		values.append(""+realQutoes.getLow()+" ,");
		values.append(""+realQutoes.getPrice()+" ,");
		values.append(""+realQutoes.getPrice()+" ,");
		values.append("'"+realQutoes.getDay()+"' ,");
		values.append("'"+realQutoes.getTime()+"' ,");
		values.append(""+realQutoes.getVolume()+" ,");
		values.append(""+realQutoes.getTurnvolume()+" ,");
		values.append(""+realQutoes.getZdbz()+" ,");
		values.append("'"+realQutoes.getIndustry()+"' ,");
		values.append("sysdate");
		values.append(")");
		if(Boolean.valueOf(PropertyUtil.get("showtestqutoes"))){
			if(realQutoes.getCode().equals(PropertyUtil.get("testcode"))){
				log.info(insert+values.toString());
			}		
		}
		return insert+values.toString();
	}
	public Map  myConcernJson(String userid){
		String sql = " select * from realtimequtoes where "
				+ "  code_ in (SELECT code_ FROM USERCONCERN where  userid='"
				+ userid + "') order by VOLUME desc";
		List<Map<String, Object>> data = this.jdbcTemplate.queryForList(sql);
		try {
			if (data != null && data.size() > 0) {
				int count = data.size();
				for (int i = 0; i < count; i++) {
					double curPrice = Double.valueOf(data.get(i).get("PRICE_")
							.toString());
					double prePrice = Double.valueOf(data.get(i)
							.get("PRECLOSE_").toString());
					double openPrice = Double.valueOf(data.get(i).get("OPEN_")
							.toString());
					double number = Double.valueOf(data.get(i).get("VOLUME")
							.toString());
					double money = Double.valueOf(data.get(i).get("TURNVOLUME")
							.toString());
					data.get(i).put("VOLUME",
							DataUtils.roundDouble(number / 10000, 2));
					data.get(i).put("TURNVOLUME",
							DataUtils.roundDouble(money / 10000, 2));
					String code = data.get(i).get("CODE_").toString();
					data.get(i).put("CODEONLY",
							code.substring(2, code.length()));
					data.get(i).put("A1",
							DataUtils.roundDouble(curPrice - prePrice, 2));
					if (openPrice == 0) {// 停牌
						data.get(i).put("A", "--");
						data.get(i).put("color", "gray");
					} else {
						data.get(i).put(
								"A",
								DataUtils.roundDouble((curPrice - prePrice)
										* 100 / prePrice, 2)
										+ "%");
						if ((curPrice - prePrice) > 0) {
							data.get(i).put("color", "red");
						} else if ((curPrice - prePrice) < 0) {
							data.get(i).put("color", "green");
						} else {
							data.get(i).put("A", "--");
							data.get(i).put("color", "gray");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("data", data);
		map.put("time", DateUtil.format(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
		return map;
	}
	public synchronized void exeSqls(List<String> sqls){
		String arr[] = sqls.toArray(new String[sqls.size()]);
		this.jdbcTemplate.batchUpdate(arr);
		
	}
	public int updateRealtimeBatch(List<RealQutoes> list) throws DataAccessException {  
	    final List<RealQutoes> temList = list;  
	    StringBuffer sql = new StringBuffer("");
		sql.append(" update REALTIMEQUTOES set ");
		sql.append(" low_ = ?");
		sql.append(" ,high_ =?");
		sql.append(" ,open_ =?");
		sql.append(" ,price_ =?");
		sql.append(" ,close_ =?");
		sql.append(" ,preclose_ =?");
		sql.append(" ,volume =?");
		sql.append(" ,turnvolume =?");
		sql.append(" ,zdbz =?");
		sql.append(" ,day_ =?");
		sql.append(" ,time_ =?");
		sql.append(" ,OPER_TIME =sysdate" );
		sql.append(" where code_=?");
	    int[] ii = null; 
	    try{  
	       ii = this.jdbcTemplate.batchUpdate(sql.toString(), new MyBatchPreparedStatementSetterUpdate(temList));
	    }catch (org.springframework.dao.DataAccessException e) {  
	        e.printStackTrace();  
	    }
		return ii.length;  
	}  
	  
	private class MyBatchPreparedStatementSetterUpdate implements BatchPreparedStatementSetter{  
	    final List<RealQutoes> temList;  
	      
	    public MyBatchPreparedStatementSetterUpdate(List<RealQutoes> list){  
	        temList = list;  
	    }  
	    public int getBatchSize() {  
	        return temList.size();  
	    }  
	  
	    public void setValues(PreparedStatement ps, int i)  
	            throws SQLException {  
	    	RealQutoes realQutoes = temList.get(i);
	    	ps.setString(1,realQutoes.getLow());ps.setString(2,realQutoes.getHigh());
			ps.setString(3,realQutoes.getOpen());ps.setString(4,realQutoes.getPrice());
			ps.setString(5,realQutoes.getPrice());ps.setString(6,realQutoes.getPreClose());
			ps.setString(7,realQutoes.getVolume());ps.setString(8,realQutoes.getTurnvolume());
			ps.setString(9,realQutoes.getZdbz());ps.setString(10,realQutoes.getDay());
			ps.setString(11,realQutoes.getTime());ps.setString(12,realQutoes.getCode());
	    }  
	}
	public int insertRealtimeBatch(List<RealQutoes> list) throws DataAccessException {  
	    final List<RealQutoes> temList = list;  
	    String sql = "insert into REALTIMEQUTOES  (id_,record_date_,name_,code_,open_,preclose_,"
	    		+ " high_,low_,price_,close_,day_,time_,volume,turnvolume,zdbz,industry,oper_time)"
	    		+ " values (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,sysdate)" ;  
	    int[] ii = null; 
	    try{  
	       ii = this.jdbcTemplate.batchUpdate(sql, new MyBatchPreparedStatementSetter(temList));
	    }catch (org.springframework.dao.DataAccessException e) {  
	        e.printStackTrace();  
	    }
		return ii.length;  
	}  
	  
	private class MyBatchPreparedStatementSetter implements BatchPreparedStatementSetter{  
	    final List<RealQutoes> temList;  
	      
	    public MyBatchPreparedStatementSetter(List<RealQutoes> list){  
	        temList = list;  
	    }  
	    public int getBatchSize() {  
	        return temList.size();  
	    }  
	  
	    public void setValues(PreparedStatement ps, int i)  
	            throws SQLException {  
	    	RealQutoes realQutoes = temList.get(i);
	    	ps.setString(1,realQutoes.getId());
			ps.setString(2,DateUtil.date2long8(new Date()).toString());
			ps.setString(3,realQutoes.getName());
			ps.setString(4,realQutoes.getCode());
			ps.setString(5,realQutoes.getOpen());
			ps.setString(6,realQutoes.getPreClose());
			ps.setString(7,realQutoes.getHigh());
			ps.setString(8,realQutoes.getLow());
			ps.setString(9,realQutoes.getPrice());
			ps.setString(10,realQutoes.getPrice());
			ps.setString(11,realQutoes.getDay());
			ps.setString(12,realQutoes.getTime());
			ps.setString(13,realQutoes.getVolume());
			ps.setString(14,realQutoes.getTurnvolume());
			ps.setString(15,realQutoes.getZdbz());
			ps.setString(16,realQutoes.getIndustry());
	    }  
	}
	public Map<String,Object> kJson(String code){
		Map<String,Object> map = new HashMap<String,Object>();
		if (code != null && !"".equals(code)) {
			String sql = "SELECT DAY_,nvl(CLOSE_,0) as PRECLOSE_,nvl(OPEN_,0) AS OPEN_,nvl(HIGH_,0) AS HIGH_,nvl(LOW_,0) AS LOW_  FROM CLOSEQUTOES where   code_='"
					+ code.trim() + "'and open_ >0  order by day_  asc";
			List<Map<String, Object>> list = jdbcTemplate.queryForList(sql);
			List<String> dateList = new ArrayList<String>();
			List<List<String>> lst = new ArrayList<List<String>>();
			List<String> k = null;
			if (list != null && list.size() > 0) {
				for (Map<String,Object> tmp : list) {
					try{
						k = new ArrayList<String>();
						dateList.add(tmp.get("DAY_").toString());
						k.add(tmp.get("OPEN_").toString());
						k.add(tmp.get("PRECLOSE_").toString());
						k.add(tmp.get("LOW_").toString());
						k.add(tmp.get("HIGH_").toString());
						// 开盘，收盘，最低，最高
						lst.add(k);
					}catch(Exception e){}
				}
			} else {}
			long time = DateUtil.date2HHmmss(new Date());
			if(time<=153000&&time>90000){
				// 在行情交易时间内，新增一个当日k点,每个交易日凌晨5点获取上一个交易日的收盘行情，所以在开盘时间9：30-5:00之间都需要
				try {
					String sql_k = "SELECT * FROM realtimequtoes where code_='"+ code+ "' and open_>0";
					log.debug(sql_k);
					KPoint todayk = jdbcTemplate.queryForObject(sql_k, new KPoint());
					k = new ArrayList<String>();
					k.add(todayk.getOpen().toString());
					k.add(todayk.getPrice().toString());
					k.add(todayk.getLow().toString());
					k.add(todayk.getHigh().toString());
					lst.add(k);
					dateList.add(DateUtil.date2long8(new Date()).toString());
				} catch (Exception e) {
					log.debug("当日最新K点获取异常！可忽略。" + e.toString());
				}
			}
			//map.put("name", QutoesContants.STOCKMAP.get(code.trim()));
			map.put("ks", lst);
			//map.put("code", code);
			map.put("dates", dateList);
		}
		return map;
	}
	public void exeSql(String sql){
		this.jdbcTemplate.execute(sql);
	}
}
