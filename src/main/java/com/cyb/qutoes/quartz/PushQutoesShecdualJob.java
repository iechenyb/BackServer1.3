package com.cyb.qutoes.quartz;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.cyb.push.PushConstant;
import com.cyb.push.PushServer;
import com.cyb.qutoes.dao.GrabDataDao;

@Component
public class PushQutoesShecdualJob {
	Log log = LogFactory.getLog(PushQutoesShecdualJob.class);
	@Resource
	JdbcTemplate jdbcTemplate;
	@Resource
	public GrabDataDao grabDataDao;
	@Scheduled(cron="00 * 09-16 ? * MON-FRI")
	public void pushByMinuteNoon(){
		log.info("一分钟行情推送完毕！");
	}
	 @Scheduled(cron="*/3 * 05-11,13-15 ? * MON-FRI")
		public void pushMyConcern(){
	    	try{
	    	  Set<String> codes = PushConstant.conerns.keySet();
			  if(!codes.isEmpty()){
				  Iterator<String> it = codes.iterator();
				  while(it.hasNext()){
					  String codecur = it.next();
					  Map<?, ?> map = grabDataDao.myConcernJson(codecur);
					  if(!map.isEmpty()){
						  if(PushConstant.conerns.get(codecur).isChannelOpen()){
							  log.debug("push myconcern code="+codecur+","+map);
							  PushServer.sendToAllClientOneByOne(codecur+PushConstant.MYCONCERN,map);
						  }
					  }
				  }
			  }
		    } catch (Exception e) {
				e.printStackTrace();
			}
		 log.debug("push myconcern over!");
	    }
	@Scheduled(cron="*/3 * 05-15 ? * MON-FRI")
	public void pushMinqutoes(){
    	try {
    		  /*for(Map<String,Object> stock:PushConstant.qutoesCodesList){
    			  String codecur = stock.get("code").toString();
				  Map<String, Object> map = grabDataDao.lineJson(codecur);
				  Map<String, Object> mapk = grabDataDao.kJson(codecur);
				  if(!map.isEmpty()&&mapk.get("ks")!=null){
					  map.putAll(mapk);
					  map.put("ks", mapk.get("ks"));
					  map.put("dates", mapk.get("dates"));
					  PushServer.sendToAllClientOneByOne(codecur+PushConstant.MINQUTOES,map);
				  }
    		  }*/
			  Set<String> codes = PushConstant.qutoesCodes.keySet();
			  if(!codes.isEmpty()){
				  Iterator<String> it = codes.iterator();
				  while(it.hasNext()){
					  String codecur = it.next();
					  Map<String, Object> map = grabDataDao.lineJson(codecur);
					  Map<String, Object> mapk = grabDataDao.kJson(codecur);
					  if(!map.isEmpty()&&mapk.get("ks")!=null){
						  map.putAll(mapk);
						  map.put("ks", mapk.get("ks"));
						  map.put("dates", mapk.get("dates"));
						  log.debug("push min code="+codecur+","+map);
						  PushServer.sendToAllClientOneByOne(codecur+PushConstant.MINQUTOES,map);
						  log.debug(map);
					  }
				  }
			  }  
		} catch (Exception e) {
			e.printStackTrace();
		}
    	log.debug("push minutequtoes over!");
    }
}
