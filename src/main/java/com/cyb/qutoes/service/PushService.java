package com.cyb.qutoes.service;

import java.util.Map;

import org.springframework.web.socket.WebSocketSession;

public interface PushService {
	public Map pushMyConcernQutoes(WebSocketSession session);
	public Map pushMintuesQutoes(WebSocketSession session);
}
