package com.cyb.qutoes.utils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;

public class QutoesUtils {
	
  public static List<String> spiltTime(String type,int step){
	  List<String> lst = new ArrayList<String>();

		String day = DateUtil.date2long8(new Date()).toString(); //20150102
		Calendar openNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "start_noon"));
        Calendar closeNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "end_noon"));
        Calendar openAfterNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "start_afternoon"));
        Calendar closeAfterNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "end_afternoon"));
	    long openNoonMillis = openNoon.getTimeInMillis();
	    long closeNoonMillis = closeNoon.getTimeInMillis();
	    for(long i = openNoonMillis;i<=closeNoonMillis;i=i+1000*60*step){
	    	 Calendar x = Calendar.getInstance();
	    	 x.setTimeInMillis(i);
	    	 String str= DateUtil.date2long14(x.getTime()).toString();
	    	 lst.add(str.substring(8,12));
	    }
	    long openAfterNoonMillis = openAfterNoon.getTimeInMillis();
	    long closeAfterNoonMillis = closeAfterNoon.getTimeInMillis();
	    for(long i = openAfterNoonMillis;i<=closeAfterNoonMillis;i=i+1000*60*step){
	    	 Calendar x = Calendar.getInstance();
	    	 x.setTimeInMillis(i);
	    	 String str= DateUtil.date2long14(x.getTime()).toString();
	    	 lst.add(str.substring(8,12));
	    }
	    
	  return lst;
  }
  public static void main(String[] args) {
//	  spiltTime("noon",0);
	  Calendar openNoon = DateUtil.Calendar("20151229093000");
	  long openNoonMillis = openNoon.getTimeInMillis();
	  openNoonMillis = openNoonMillis+60*1000;
			  //openNoon.getTime().getTime();
	  Calendar x = Calendar.getInstance();
	  x.setTimeInMillis(openNoonMillis);
	  System.out.println(DateUtil.date2long14(x.getTime()));
	  //	  DateUtil.date2long14(date0)
	  
}
}
