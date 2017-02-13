package com.cyb.ws;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import net.sf.json.JSONObject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;

import com.cyb.qutoes.contants.QutoesContants;


public class SystemWebSocketHandler implements WebSocketHandler {
	 
    private static final Log  logger = LogFactory.getLog(SystemWebSocketHandler.class);
    
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String userName = (String) session.getHandshakeAttributes().get("code");
        String type = (String) session.getHandshakeAttributes().get("type");
        QutoesContants.sessions.add(session);//新增链接会话
        logger.info("new session！"+userName+"#"+type);
    }
    public void removeMoreSession(String userName,String type){
    	for(WebSocketSession session:QutoesContants.sessions){
    		String userName_ = (String) session.getHandshakeAttributes().get("code");
            String type_ = (String) session.getHandshakeAttributes().get("type");
            if(userName.equals(userName_)&&type.equals(type_)){
            	QutoesContants.sessions.remove(session);
            }
    	}
    }
    public void pushTheUsersOnline(){
    	Map msg = new HashMap();
    	msg.put("msgType", "users");
    	sendMessageToUsers("users",JSONObject.fromObject(msg).toString());
    }
    /*public List<String> getOnlineUsers(){
    	List<String> lst = new ArrayList<String>();
    	try {
	    	for (WebSocketSession user : users) {
	        	String userName = (String) user.getHandshakeAttributes().get("code");
	            	lst.add(userName);
	    	}
           } catch (Exception e) {
                e.printStackTrace();
            }
    	return lst;
    }*/
    
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
//    	System.out.println("SystemWebSocketHandler.handleMessage"+message);
    	/*String curUser = (String) session.getHandshakeAttributes().get("code");
    	JSONObject data = JSONObject.fromObject(message.getPayload());//
    	String content=curUser+"["+new Date().toString()+"]say:"+data.getString("msg").trim();
    	String toUser = data.getString("toUser");
    	if(toUser==null||"".equals(toUser)){
    		sendMessageToUsers("text", content);
    	}else{
    		sendMessageToUser(toUser,"text", content);
    	}*/
    }
    
    
    public  static TextMessage formateMessage(String type,String content){
    	Map msg = new HashMap();
    	msg.put("msgType", type);
    	msg.put("content",content);
    	return new TextMessage(JSONObject.fromObject(msg).toString());
    }
    public String getUserName(WebSocketSession session){
    	return (String) session.getHandshakeAttributes().get("code");
    }
    public static void main(String[] args) {
    	String str =  "{msg:'Here is a message!',toUser:4}";
    	JSONObject XX = JSONObject.fromObject(str);
    	System.out.println(XX.getString("msg"));
    	System.out.println(XX.getString("toUser"));
    	double x = -0.00068+0.000205;
    	double y =1/2.0;
    	System.out.println(x);
	}
 
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
    	try {
			if(session.isOpen()){
			    session.close();
			}
			QutoesContants.sessions.remove(session);
		} catch (Exception e) {
			logger.info("handleTransportError!");
		}
    }
 
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
    	QutoesContants.sessions.remove(session);
    	logger.info(session+" closed!");
    }
 
    public boolean supportsPartialMessages() {
    	logger.info("###");
        return false;
    }

    public static void  sendMessageToUsers(String type,String content) {
        for (WebSocketSession user :  QutoesContants.sessions) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(formateMessage(type, content));
                }
            } catch (IOException e) {
               logger.info("push exception!\n"+e.toString());
            }
        }
    }
    public static void  sendMessageToUser(WebSocketSession user,String type,String content) {
            try {
                if (user.isOpen()) {
                    user.sendMessage(formateMessage(type, content));
                }
            } catch (IOException e) {
            	logger.info("push exception!\n"+e.toString());
            }
    }
 
    public static void sendMessageToUser(String userName, String type,String content) {
        for (WebSocketSession user :  QutoesContants.sessions) {
            if (user.getHandshakeAttributes().get("code").equals(userName)) {
                try {
                    if (user.isOpen()) {
                        user.sendMessage(formateMessage("text", content));
                    }
                } catch (IOException e) {
                	logger.info("push exception!\n"+e.toString());
                }
                break;
            }
        }
    }
}