package com.cyb.qutoes.quartz;

import java.util.concurrent.BlockingQueue;

import net.sf.json.JSONArray;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.socket.WebSocketSession;

import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.service.PushService;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.SpringUtil;
import com.cyb.ws.SystemWebSocketHandler;
/**
 * 废弃不用
 * @author DHUser
 *
 */
public class MessageThread implements Runnable{
	Log log = LogFactory.getLog(MessageThread.class);
	JdbcTemplate jdbcTemplate;
	PushService pushService;
	BlockingQueue<WebSocketSession> sessions;
	
	public MessageThread(){
		this.jdbcTemplate = (JdbcTemplate)SpringUtil.getBean("jdbcTemplate");
		this.pushService = (PushService)SpringUtil.getBean("pushService");
		this.sessions = QutoesContants.sessions;
	}
	public void run() {
		Thread.currentThread().setName("socketio推送线程");
		log.info("pushstart!");
		PushManager.pushServerState = true;
		while(QutoesContants.SWTICH){/*
			try {
			    Thread.sleep(1000*Integer.valueOf(PropertyUtil.getValueByKey("App", "pushTimeInter")));
				for(WebSocketSession session:sessions){
					if(session.isOpen()){
						String code = (String) session.getHandshakeAttributes().get("code");
					    String type = (String) session.getHandshakeAttributes().get("type");
						if(type!=null&&!"".equals(type)){
							if(type.equals(QutoesContants.minqutoesMessage)){//推送分钟行情
								//pushService.pushMintuesQutoes(session);
								//SystemWebSocketHandler.sendMessageToUser(session,code+"#"+type, JSONArray.fromObject(pushService.pushMintuesQutoes(session)).toString());
								log.info("WebSocket推送 "+code+"#"+type);
							}else if(type.equals(QutoesContants.myConcernMessage)){
								//pushService.pushMyConcernQutoes(session);
								//SystemWebSocketHandler.sendMessageToUser(session,code+"#"+type, JSONArray.fromObject(pushService.pushMyConcernQutoes(session)).toString());
								log.info("WebSoket推送 "+code+"#"+type);
							}
						}
					 }else{//end if open
						QutoesContants.sessions.remove(session);//链接断开则不发送消息		
					 }		
		    }//end for
		} catch (Exception e) {
				e.printStackTrace();
				PushManager.pushServerState = false;
		}
     */}
	PushManager.pushServerState = false;
	log.info("pushend!");
  }
}

