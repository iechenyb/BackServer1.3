package com.cyb.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class QutoesUtils {
	public static List<String> initSpace(int len){
		  List<String> lst = new ArrayList<String>();
		  for(int i=0;i<len;i++){
			  lst.add("-");
		  }
		  return lst;
	  }
  public static List<String> spiltTimeList(String type,int step){
	  List<String> lst = new ArrayList<String>();
		String day = DateUtil.date2long8(new Date()).toString(); //20150102
		Calendar openNoon = DateUtil.Calendar(day+"093000");
        Calendar closeNoon = DateUtil.Calendar(day+"113000");
        Calendar openAfterNoon = DateUtil.Calendar(day+"130000");
        Calendar closeAfterNoon = DateUtil.Calendar(day+"150000");
	    long openNoonMillis = openNoon.getTimeInMillis();
	    long closeNoonMillis = closeNoon.getTimeInMillis();
	    for(long i = openNoonMillis;i<=closeNoonMillis;i=i+1000*60*step){
	    	 Calendar x = Calendar.getInstance();
	    	 x.setTimeInMillis(i);
	    	 String str= DateUtil.date2long14(x.getTime()).toString();
	    	 lst.add(str.substring(8,10)+":"+str.substring(10,12));
	    }
	    long openAfterNoonMillis = openAfterNoon.getTimeInMillis();
	    long closeAfterNoonMillis = closeAfterNoon.getTimeInMillis();
	    for(long i = openAfterNoonMillis;i<=closeAfterNoonMillis;i=i+1000*60*step){
	    	 Calendar x = Calendar.getInstance();
	    	 x.setTimeInMillis(i);
	    	 String str= DateUtil.date2long14(x.getTime()).toString();
	    	 lst.add(str.substring(8,10)+":"+str.substring(10,12));
	    }
	    
	  return lst;
  }
  
  public static Map<String,String> spiltTimeMap(String type,int step){
	    Map<String,String> map = new LinkedHashMap<String,String>();
		String day = DateUtil.date2long8(new Date()).toString(); //20150102
		Calendar openNoon = DateUtil.Calendar(day+"093000");
        Calendar closeNoon = DateUtil.Calendar(day+"113000");
        Calendar openAfterNoon = DateUtil.Calendar(day+"130000");
        Calendar closeAfterNoon = DateUtil.Calendar(day+"150000");
	    long openNoonMillis = openNoon.getTimeInMillis();
	    long closeNoonMillis = closeNoon.getTimeInMillis();
	    for(long i = openNoonMillis;i<=closeNoonMillis;i=i+1000*60*step){
	    	 Calendar x = Calendar.getInstance();
	    	 x.setTimeInMillis(i);
	    	 String str= DateUtil.date2long14(x.getTime()).toString();
	    	 map.put(str.substring(8,10)+":"+str.substring(10,12),"-");
	    }
	    long openAfterNoonMillis = openAfterNoon.getTimeInMillis();
	    long closeAfterNoonMillis = closeAfterNoon.getTimeInMillis();
	    for(long i = openAfterNoonMillis;i<=closeAfterNoonMillis;i=i+1000*60*step){
	    	 Calendar x = Calendar.getInstance();
	    	 x.setTimeInMillis(i);
	    	 String str= DateUtil.date2long14(x.getTime()).toString();
	    	 map.put(str.substring(8,10)+":"+str.substring(10,12),"-");
	    }
	  return map;
  }
  @SuppressWarnings({ "unchecked", "rawtypes" })
public   static void   sort()   { 
	  Map<String,String> map = new HashMap<String,String>();
	  @SuppressWarnings("unused")
	  Set<String>  keySet     =   map.keySet(); 
	  for(int i=10;i<59;i++){
		  map.put("15:"+i, "15:"+i);
	  };
      List<String>   list   =   new   ArrayList<String>(map.keySet()); 
      Collections.sort(list,   new   Comparator<Object>()   { 
              public   int   compare(Object   a,   Object   b)   { 
                      return   a.toString().toLowerCase().compareTo(b.toString() 
                              .toLowerCase()); 
              } 
      }); 

      keySet   =   new   TreeSet(list); 
      System.out.println(map);
} 
  
  public static Map<String,Double> calSection(double max,double min,double preclose){
	  Map<String,Double> result = new HashMap<String, Double>();
	  if( min <  preclose && max >  preclose) {
	       	if((preclose-min)>(max - preclose)){//下侧高
	       		max = preclose+(preclose-min);
	       	}else{
	       		min = preclose-(max - preclose);
	       	}
       }else if(min==preclose&&max!=preclose){
       	min = min-(max-preclose);
       }else if(max==preclose&&min!=preclose){
       	max = max + (preclose-min);
       }else if(min<max&&max<preclose){//收盘线下测
       	max = preclose+(max-min);
       }else if(max>min&&min>preclose){
       	min = preclose - (max-min) ;
       }else if(max ==0 && min==0 && preclose!=0){//stop trade
       	max = 2*preclose;
       }else if(max >preclose && min>preclose && max==min){ // add to 10%
       	max = preclose +(max-preclose);
       	min = preclose -(min-preclose);
       }else if(max <preclose && min<preclose && max==min){//sub to 10%
       	max = preclose +(preclose-max);
       	min = preclose -(preclose-min);
       }
	  result.put("min",min);
	  result.put("max", max);
	  return result;
  }
  public static int getIndexOfNotNullValueList(List<String> lst){
	  int idx=0;
	  for(int j=0;j<lst.size();j++){
		  if(!lst.get(j).equals("-")){
			  idx=j;
		  }
	  }
	  return idx;
  }

  public static void main(String[] args) {
	  System.out.println(spiltTimeMap("",1));
	  
}
}
