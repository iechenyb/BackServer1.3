package com.cyb.ws;

import java.util.Map;

import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.socket.WebSocketHandler;

import com.cyb.qutoes.MessageEntity;

public class WebSocketHandshakeInterceptor implements org.springframework.web.socket.server.HandshakeInterceptor {
    private static Log logger = LogFactory.getLog(WebSocketHandshakeInterceptor.class);
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, 
    		WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            String code = ((ServletServerHttpRequest) request).getServletRequest().getParameter("code");
            String type = ((ServletServerHttpRequest) request).getServletRequest().getParameter("type");
            if (code != null&&type!=null) {
                attributes.put("code",code); 
                attributes.put("type",type);
            }
        }
        return true;
    }
 
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
    	logger.info("******");
    }
}