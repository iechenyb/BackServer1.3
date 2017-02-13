package com.cyb.push;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.DataListener;

public class PushListener implements DataListener<Object>{
  public static Logger log = LoggerFactory.getLogger(PushListener.class);
  SocketIOServer server;
  public PushListener(SocketIOServer server){
  	this.server = server;
  }
  /**
   * 统计需要推送行情的客户端
   */
  public void onData(SocketIOClient client, Object action, AckRequest req)  {
	  try{
		log.debug("有客户主动请求数据，"+action+",client:"+client);
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("x", action.toString());
		PushServer.sendBroadcast("msg",map);
		
		JSONObject para = JSONObject.fromObject(action);
		String type = para.get("type").toString();
		String code = para.get("code").toString();
		
		if(PushConstant.MINQUTOES.equals(type)){
			/*Map<String,Object> minQutoes = new LinkedHashMap<String,Object>();
			minQutoes.put("type", type);
			minQutoes.put("code", code);
			minQutoes.put("client", client);
			PushConstant.qutoesCodesList.add(minQutoes);*/
			PushConstant.qutoesCodes.put(code,client);
		}		
	  }catch(Exception e){
		  log.info(e.toString()+",param="+JSONObject.fromObject(action).toString());
	  }
  }
}