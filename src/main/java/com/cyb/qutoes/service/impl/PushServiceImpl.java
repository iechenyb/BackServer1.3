package com.cyb.qutoes.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;

import com.cyb.qutoes.service.PushService;
import com.cyb.utils.Contants;
import com.cyb.utils.DataUtils;
import com.cyb.utils.DateUtil;
import com.cyb.utils.QutoesUtils;
import com.cyb.utils.SpringUtil;
@Service("pushService")
public class PushServiceImpl implements PushService{
	static Log log = LogFactory.getLog(PushServiceImpl.class);
	 @Resource(name="jdbcTemplate")
	 JdbcTemplate jdbcTemplate ;
	public Map pushMyConcernQutoes(WebSocketSession session) {
		String username = (String) session.getHandshakeAttributes().get("code");
	    String type = (String) session.getHandshakeAttributes().get("type");
		 String sql= " select * from realtimequtoes where "+
				  "  code_ in (SELECT code_ FROM USERCONCERN where  userid='"+username+"') order by VOLUME desc";
		 List<Map<String, Object>> data = this.jdbcTemplate.queryForList(sql);
		 if(data!=null&&data.size()>0){
			 int count = data.size();
			 for(int i=0;i<count;i++){
				 double curPrice = Double.valueOf(data.get(i).get("PRICE_").toString());
				 double prePrice = Double.valueOf(data.get(i).get("PRECLOSE_").toString());
				 double openPrice = Double.valueOf(data.get(i).get("OPEN_").toString());
				 double number = Double.valueOf(data.get(i).get("VOLUME").toString());
				 double money = Double.valueOf(data.get(i).get("TURNVOLUME").toString());
				 data.get(i).put("VOLUME",DataUtils.roundDouble(number/10000,2));
				 data.get(i).put("TURNVOLUME",DataUtils.roundDouble(money/10000,2));
				 String code = data.get(i).get("CODE_").toString();
				 data.get(i).put("CODEONLY", code.substring(2, code.length()));
				 data.get(i).put("A1", DataUtils.roundDouble(curPrice-prePrice,2));
				 if(openPrice==0){//停牌
					 data.get(i).put("A", "--");
					 data.get(i).put("color", "gray");
				 }else{
					 data.get(i).put("A", DataUtils.roundDouble((curPrice-prePrice)*100/prePrice,2)+"%");
					 if((curPrice-prePrice) > 0){
						 data.get(i).put("color", "red");
					 }else if ((curPrice-prePrice) < 0){
					     data.get(i).put("color", "green");
					 }else{
						 data.get(i).put("A", "--");
						 data.get(i).put("color", "gray");
					 }
				 }
			 }
		 }
		 Map map = new HashMap();
		 map.put("time", DateUtil.format(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
		 map.put("data", data);
		 return map;
	}

	public Map pushMintuesQutoes(WebSocketSession session) {
		String code = (String) session.getHandshakeAttributes().get("code");
	    String type = (String) session.getHandshakeAttributes().get("type");
	  	Map map = new HashMap();
		String jyrsql = "(select  curjyr from STOCKCONFIG where alias='LASTESTDAY' )";
	  	if(code!=null&&!"".equals(code)){
	  		
	  		String sql_zs="SELECT (price_-preCLOSE_ )/preclose_*100  as A,price_ as price,code_,name_ FROM realtimequtoes "
		    		+ " where industry='zhzs'  group by code_";
		    List<Map<String,Object>> list_zhzs = this.jdbcTemplate.queryForList(sql_zs);
		    String shzsprice = "0";
		    String szzsprice = "0";
		    String shzsA = "0";
		    String szzsA = "0";
		    String colorsh="green";
		    String colorsz="green";
		    if(list_zhzs!=null&&list_zhzs.size()>0){
		    	for(Map zhzs :list_zhzs){
		    		String code_ = zhzs.get("CODE_").toString();
		    		if("sz399001".equals(code_)){
		    			szzsprice =zhzs.get("PRICE").toString();
		    			szzsA = DataUtils.roundStr(Double.valueOf(zhzs.get("A").toString()), 2);
		    			if(Double.valueOf(szzsA)>0){
		    				szzsA="+"+szzsA;
		    				colorsz="red";
		    			}
		    		}else if("sh000001".equals(code_)){
		    			shzsprice =zhzs.get("PRICE").toString();
		    			shzsA = DataUtils.roundStr(Double.valueOf(zhzs.get("A").toString()), 2);
		    			if(Double.valueOf(shzsA)>0){
		    				shzsA="+"+shzsA;
		    				colorsh="red";
		    			}
		    		}
		    	}
		    }
		    
		    
		    	JdbcTemplate dao = (JdbcTemplate) SpringUtil.getBean("jdbcTemplate");
		    	String sql = "SELECT substr(oper_time,0,16)  as time_ ,price_,nvl(PRECLOSE_,0) as PRECLOSE_,HIGH_,LOW_,OPEN_,time_min FROM MINUTEQUTOESCURRDAY where record_date_ = "+jyrsql+" and code_='"+code.trim()+"' order by time_ asc";
		        List<Map<String,Object>> list = dao.queryForList(sql);
		        log.info(sql);
		        List<String> y = new ArrayList<String>();
		        List<String> x_ = new ArrayList<String>();
		        List<String> x = QutoesUtils.spiltTimeList("", 1);//获取所有的坐标
		        Map<String,String> allMap = QutoesUtils.spiltTimeMap("", 1);//获取所有的坐标
		        double max = 0.0;double min = 0.0;double preclose=0.0;double curPrice=0.0;double open = 0.0;
		        if(list!=null&&list.size()>0){
		        	open = Double.valueOf( list.get(0).get("OPEN_").toString());
		        	preclose = Double.valueOf( list.get(0).get("PRECLOSE_").toString());
		        	for(Map tmp:list){
		        		curPrice = Double.valueOf(tmp.get("PRICE_").toString());
		        		String time_ = tmp.get("TIME_MIN").toString();
		        		String res = DateUtil.format(time_+"00", "yyyy-MM-dd HH:mm:ss");
		        		allMap.put(res.substring(11, res.length()-3), tmp.get("PRICE_").toString());
		        		x_.add(res.substring(11, res.length()-3));
		        	}
		        	max = Double.valueOf(list.get(list.size()-1).get("HIGH_").toString());
		        	min = Double.valueOf(list.get(list.size()-1).get("LOW_").toString());
		        	for(int i=0;i<x.size();i++){
		        		String time_str = x.get(i);//09:30
		        		String val_str = allMap.get(time_str).trim();
		        	    y.add(val_str);
		        	}
		        	int maxMin = Integer.valueOf(x_.get(x_.size()-1).replace(":", ""));
		        	Iterator<String> x1 = x.iterator();  
		            Iterator<String> y1 = y.iterator();  
		            while(x1.hasNext()&&y1.hasNext()){  
		            	 int tmpx = Integer.valueOf(x1.next().replace(":", ""));
		            	 String tmpy = y1.next();
		                 if(tmpy.equals("-")&&tmpx<maxMin){  
		                     //移除当前的对象  
		                     x1.remove();  
		                     y1.remove();  
		                 }  
		             } 
		        	if(x!=null&&x.size()>0){
		        		map.put("x", JSONArray.fromObject(x));
		        	}else{
		        		map.put("x", JSONArray.fromObject("[]"));
		        	}
		        	if(y!=null&&y.size()>0){
		        		map.put("y", JSONArray.fromObject(y));
		        	}else{
		        		map.put("y", JSONArray.fromObject("[]"));
		        	}
		        	map.put("realmax", max); map.put("realmin", min);
		        	Map<String,Double> res = QutoesUtils.calSection(max, min, preclose);
		        	max = res.get("max");
		        	min = res.get("min");
		        }else{
		        	return new HashMap();
		        }
	        double amplitude  =  0;
		    if(preclose!=0){
		    	amplitude = 100*(curPrice-preclose)/preclose;  
		    }
		    map.put("shzsprice", shzsprice);
		    map.put("szzsprice", szzsprice);
		    map.put("shzsA", shzsA);
		    map.put("szzsA", szzsA);
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
	        map.put("start","09:30");
	        map.put("end","15:30");
	        map.put("time", DateUtil.format(new Date(), "yyyy年MM月dd日HH时mm分ss秒"));
	  	}
	   return map;	  
	}
}
