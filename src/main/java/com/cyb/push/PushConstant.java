package com.cyb.push;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.corundumstudio.socketio.SocketIOClient;
public class PushConstant {
 public static String MINQUTOES="MINQUTOES";
 public static String KQUTOES="KQUTOES";
 public static String ZDFX="ZDFX";
 public static String MYCONCERN="myConcern";
 public static Map<String,SocketIOClient> qutoesCodes = new LinkedHashMap<String,SocketIOClient>();
 public static List<Map<String,Object>> qutoesCodesList = new ArrayList<Map<String,Object>>();
 public static Map<String,SocketIOClient> conerns = new LinkedHashMap<String,SocketIOClient>();
}
