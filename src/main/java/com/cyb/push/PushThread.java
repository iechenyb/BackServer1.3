package com.cyb.push;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class PushThread  implements Runnable{
	@Resource(name = "grabDataDao")
	public GrabDataDao dao;
	public PushThread(GrabDataDao dao){
		this.dao = dao;
	}
	Logger log = LoggerFactory.getLogger(PushThread.class);
	public void run() {
		Thread.currentThread().setName("消息推送线程");
		while (true) {
			try {
				try {
					if(DateUtil.between(PropertyUtil.get("start_noon"), PropertyUtil.get("end_noon"))||
							DateUtil.between(PropertyUtil.get("start_afternoon"), PropertyUtil.get("end_afternoon"))){
						try {
							List<Map<String, Object>> data =dao.staticsCompany();
							  Map<String,Object> dpzs = dao.hsindiator();
							  Map<String,Object> ret = new HashMap<String,Object>();
							  ret.put("dpzs", dpzs);
							  ret.put("lst", data);
							  ret.put("cur",DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
							  log.debug("push pie:"+ret);
							  PushServer.sendToAllClientOneByOne("pie",ret);
						} catch (Exception e) {
								e.printStackTrace();
						}
						  
						try {
							Set<String> codes = PushConstant.qutoesCodes.keySet();
							  if(!codes.isEmpty()){
								  Iterator<String> it = codes.iterator();
								  while(it.hasNext()){
									  String codecur = it.next();
									  Map map = dao.lineJson(codecur);
									  if(!map.isEmpty()){
									  log.debug("push code="+codecur+","+map);
									  PushServer.sendBroadcast(codecur,map);
									  }
								  }
							  }
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
					Map<String,String> isTrade = new HashMap<String, String>();
					isTrade.put("isTrade", String.valueOf(QutoesContants.SWTICH));
					PushServer.sendBroadcast("trading",isTrade);
					Thread.sleep(5*1000);
				} catch (Exception e) {
					log.info("push ocur exception don't care....");
				}//广播推送消息
			} catch (Exception e) {
				log.info("push logic ocur exception, don't care....");
			}
		}
	}
}
