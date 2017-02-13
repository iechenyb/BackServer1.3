package com.cyb.mulitthread;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import sun.misc.*; 
public class Base64 {
	public static String getBase64(String str) {
		byte[] b = null;
		String s = null;
		try {
			b = str.getBytes("utf-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		if (b != null) {
			s = new BASE64Encoder().encode(b);
		}
		return s;
	}

	public static String getFromBase64(String s) {
		byte[] b = null;
		String result = null;
		if (s != null) {
			BASE64Decoder decoder = new BASE64Decoder();
			try {
				b = decoder.decodeBuffer(s);
				result = new String(b, "utf-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return result;
	}
	
	public static void main(String[] args) throws ParseException {
		String today = "20140808";
		Map<String, String> map = new HashMap<String, String>();
		SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
		for(int i=0;i<365;i++){
			Date date = nextSomeDay(format.parse(today),i);
			int weekNumber = getWeekNumber(getFirstDayOfWeek(date));
			String endDate = format(getLasttDayOfWeek(date));
			String firstDate = format(getFirstDayOfWeek(date));
			int year = Integer.valueOf(firstDate.substring(0,4));
//			map.put(year+""+weekNumber, "["+firstDate+","+endDate+"]");
		}
	}
	public static String format(Date date){
		 java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMdd");
	        //DateFormat.getDateInstance(DateFormat.DEFAULT).format(date);
		return format2.format(date);
	}
	public static int getWeekOfYear(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setMinimalDaysInFirstWeek(7);
        c.setTime(date);
        return c.get(Calendar.WEEK_OF_YEAR);
    }
	public static int getMaxWeekNumOfYear(int year) {
	        Calendar c = new GregorianCalendar();
	        c.set(year, Calendar.DECEMBER, 31, 23, 59, 59);
	        return getWeekOfYear(c.getTime());
   }
	
	public static Date getFirstDayOfWeek(Date date) { 
		  Calendar c = new GregorianCalendar(); 
		  c.setFirstDayOfWeek(Calendar.MONDAY); 
		  c.setMinimalDaysInFirstWeek(7);
		  c.setTime(date); 
		  c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday 
		  return c.getTime (); 
	}
	public static Date getLasttDayOfWeek(Date date) { 
		  Calendar c = new GregorianCalendar(); 
		  c.setFirstDayOfWeek(Calendar.MONDAY); 
		  c.setMinimalDaysInFirstWeek(7);
		  c.setTime(date); 
		  c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()+6); // Monday 
		  return c.getTime (); 
	}
	public static int getWeekNumber(Date date){ 
	       Calendar calendar = Calendar.getInstance();
	        calendar.setFirstDayOfWeek(Calendar.MONDAY);
	        calendar.setMinimalDaysInFirstWeek(7);
	        calendar.setTime(date);
	        return calendar.get(Calendar.WEEK_OF_YEAR);
	 }  
   public static Date nextSomeDay(Date date,int i){
		 Calendar c = Calendar.getInstance();
	     c.setTime(date);
		 c.add(Calendar.DAY_OF_MONTH, i);
		 return c.getTime();
	 }
}
