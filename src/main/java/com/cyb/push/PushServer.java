package com.cyb.push;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.listener.ConnectListener;
import com.cyb.utils.PropertyUtil;

public class PushServer {
	public static Logger log = LoggerFactory.getLogger(PushServerListener.class);
	// 统计链接的客户端信息
	public  static List<SocketIOClient> clients = new ArrayList<SocketIOClient>();
	public static SocketIOServer server;
	public static boolean isStarted = false;
	public static void stopPushServer(){
		if(server!=null){
			Iterator<SocketIOClient> list =  server.getAllClients().iterator();
			while(list.hasNext()){
				SocketIOClient cl= list.next();
				if(cl.isChannelOpen()){
					cl.disconnect();
					System.out.println("客户端被后台关闭："+cl);
				}
			}
			server.stop();
			isStarted = false;
			System.out.println("推送服务已经关闭！");
		}
	}
	public static void startPushServer() {
		try {
			com.corundumstudio.socketio.Configuration config = new com.corundumstudio.socketio.Configuration();
			config.setHostname(PropertyUtil.get("puship"));
			config.setPort(Integer.valueOf(PropertyUtil.get("pushport")));
			server = new SocketIOServer(config);
			log.info("推送地址："+PropertyUtil.get("puship")+":"+Integer.valueOf(PropertyUtil.get("pushport")));
			isStarted = true;
			PushListener listener = new PushListener(server);
			server.addEventListener("msg", Object.class, listener);
			server.addConnectListener(new ConnectListener() {
				public void onConnect(SocketIOClient client) {
					log.info("新的通信建立： " + client.getSessionId());
					clients.add(client);
				}
			});
			server.start();
			log.info("puser server started!");
		} catch (Exception e) {
			log.info("push server start ocurr exception!\n"+e.toString());
		}
	}
	
	public static void addListenerToServer() {
		server.addConnectListener(new ConnectListener() {
			public void onConnect(SocketIOClient client) {
				log.debug("new sessionid=" + client.getSessionId());
				clients.add(client);
			}
		});
	}
	//为每个创建连接的客户端推送消息
	public static void sendToAllClientOneByOne(Map msg)
			throws InterruptedException {
		for (SocketIOClient c : clients) {
			c.sendEvent("pushpoint", msg);
		}
		log.debug("推送消息依次给每个客户端:" + msg + ",链接客户端个数=" + clients.size());
	}
	public static void sendToAllClientOneByOne(String key,Map msg)
			throws InterruptedException {
		for (SocketIOClient c : clients) {
			if(c.isChannelOpen()){
				c.sendEvent(key, msg);
			}
		}
		log.debug("推送消息依次给每个客户端:" + msg + ",链接客户端个数=" + clients.size());
	}
	public static void sendBroadcast(Map msg) throws InterruptedException {
		server.getBroadcastOperations().sendEvent("pushpoint", msg);
		log.debug("推送消息广播:" + msg);
	}
	
	public static void sendBroadcast(String key,Map msg) throws InterruptedException {
		server.getBroadcastOperations().sendEvent(key, msg);
		log.debug("推送消息广播:" + msg);
	}
}

