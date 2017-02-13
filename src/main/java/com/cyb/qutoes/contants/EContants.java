package com.cyb.qutoes.contants;

import java.util.HashMap;
import java.util.Map;

public class EContants {
  public static String LOGIN = "login";
  public static String pp = "0";//平盘
  public static String zt = "1";//涨停
  public static String dt = "2";//跌停
  public static String sz = "3";//上涨 sz 
  public static String xd = "4";//下跌xd 
  public static String tp = "5";//停牌tp
  
  public static Map<String,String> zdbzMap = new HashMap<String,String>();
  static{
	  zdbzMap.put(pp, "平盘");
	  zdbzMap.put(zt, "涨停");
	  zdbzMap.put(dt, "跌停");
	  zdbzMap.put(sz, "上涨");
	  zdbzMap.put(xd, "下跌");
	  zdbzMap.put(tp, "停牌");
  }	
  public static Map<String,String> zdbzMapParam = new HashMap<String,String>();
  static{
	  zdbzMapParam.put(pp, "0");
	  zdbzMapParam.put(zt, "1");
	  zdbzMapParam.put(dt, "2");
	  zdbzMapParam.put(sz, "sz");
	  zdbzMapParam.put(xd, "xd");
	  zdbzMapParam.put(tp, "tp");
  }	
}
