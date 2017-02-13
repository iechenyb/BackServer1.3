package com.cyb.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DateUtil {
   private static SimpleDateFormat formatter = null;
   public static String format(Date date,String model){
	   formatter = new SimpleDateFormat(model);
	   String dateString = formatter.format(date);
	   return dateString;
   }
   /* yyyyMMddHHmmss
    * yyyyMMddHHmmssSSS
    * yyyy-MM-dd HH:mm:ss
    */
   
   public static String format(String date,String model){
	   formatter = new SimpleDateFormat(model);
	   String dateString = formatter.format(Calendar(date).getTime());
	   return dateString;
   }
   public static Long date2long8(Date date){
	   formatter = new SimpleDateFormat("yyyyMMdd");
	   String dateString = formatter.format(date);
	   return Long.valueOf(dateString);
   }
   public static Long date2long14(Date date){
	   formatter = new SimpleDateFormat("yyyyMMddHHmmss");
	   String dateString = formatter.format(date);
	   return Long.valueOf(dateString);
   }
   public static Long date2HHmmss(Date date){
	   formatter = new SimpleDateFormat("HHmmss");
	   String dateString = formatter.format(date);
	   return Long.valueOf(dateString);
   }
   public static String timeToMilis(Date date){
	   formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
	   String dateString = formatter.format(date);
	   return dateString;
   }
   public static Calendar Calendar(String yyyymmddhhmmss){
	Calendar cal = Calendar.getInstance();
	int year = Integer.valueOf(yyyymmddhhmmss.substring(0, 4));
	int month = Integer.valueOf(yyyymmddhhmmss.substring(4, 6));
	int day = Integer.valueOf(yyyymmddhhmmss.substring(6, 8));
	int hour = Integer.valueOf(yyyymmddhhmmss.substring(8, 10));
	int min = Integer.valueOf(yyyymmddhhmmss.substring(10,12));
	int sec = Integer.valueOf(yyyymmddhhmmss.substring(12, 14));
	cal.set(year, month-1, day, hour, min, sec);
	return cal;
   }
   public static Calendar Calendar(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return cal;
	   }
   public static Date preDate(Date date){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, -1);
		return cal.getTime();
	}
   public static Date DateOffset(Date date,int offset){
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DAY_OF_MONTH, offset);
		return cal.getTime();
   }
   public static boolean between(String shhmmss,String ehhmmss){
	   String day = DateUtil.date2long8(new Date()).toString(); //20150102
		//09：:30-11:30 13:00-15:00
	   Calendar mornings = DateUtil.Calendar(day+shhmmss);
	   Calendar morninge = DateUtil.Calendar(day+ehhmmss);
	   Calendar curDate = DateUtil.Calendar(new Date());
	   if((curDate.after(mornings)&&curDate.before(morninge))){
		   return true;
	   	}else{
		   return false;
	    }
   }
   public static void main(String[] args) {
	System.out.println( preDate(new Date()));
	List<String> lst = QutoesUtils.spiltTimeList("", 1);
	List<String> subLst = lst.subList(lst.indexOf("15:00")+1, lst.size());
	System.out.println(subLst.size()+""+subLst);
	System.out.println(DateUtil.between("093000", "133100"));
}
}
