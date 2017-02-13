package com.cyb.utils;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.time.DateUtils;
import org.h2.tools.Server;

public class DateUtilsSplit {
	public static int days(){
		List<String> data = new ArrayList<String>();
		data.add("20150101");
		data.add("20150102");
		data.add("20150103");
		data.add("20150104");
		data.add("20150106");
		data.add("20150107");
		data.add("20150111");
		data.add("20150112");
		data.add("20150115");
		data.add("20150118");
		data.add("20150121");
		data.add("20150122");
		data.add("00000000");
	    String nextDay = "";
	    String curDay = "";
	    String sixZERO = "000000";
	    Map<String,List<String>> records = new HashMap<String,List<String>>();
	    int start = 0,end = 0;
	    for(int i=0 ;i<data.size();i++){
	    	System.out.print("data["+i+"]="+data.get(i)+",");
	    }
	    System.out.println();
	    int max = 0;
		for(int i=0 ;i<data.size()-1;i++){
			Date d1 = DateUtil.Calendar(data.get(i)+sixZERO).getTime();
			Date d2 = DateUtil.DateOffset(DateUtil.Calendar(data.get(i+1)+sixZERO).getTime(),-1);
			curDay = DateUtil.format(d1,"yyyyMMdd");
			nextDay = DateUtil.format(d2,"yyyyMMdd");
			if(curDay.equals(nextDay)){
				end = i+1;
				continue;
			}else{
				if((end-start+1)>max){ max = end-start+1;}
				start = end = i+1; 
			}
			  nextDay = "";
			  curDay = "";
		}
		 System.out.println();
		 System.out.println("max="+max);
		 return max;
	}
	public static int days1(){
		List<String> data = new ArrayList<String>();
		data.add("20150101");
		data.add("20150102");
		data.add("20150103");
		data.add("20150104");
		data.add("20150106");
		data.add("20150107");
		data.add("20150111");
		data.add("20150112");
		data.add("20150115");
		data.add("20150118");
		data.add("20151231");
		data.add("20160101");
		data.add("20160102");
		data.add("20160103");
		data.add("00000000");
	    Long nextDay = 0l;
	    Long curDay = 0l;
	    String sixZERO = "000000";
	    Map<String,List<String>> records = new HashMap<String,List<String>>();
	    int start = 0,end = 0;
	    for(int i=0 ;i<data.size();i++){
	    	System.out.print("data["+i+"]="+data.get(i)+",");
	    }
	    System.out.println();
	    int max = 0;
		for(int i=0 ;i<data.size()-1;i++){
			String year1 = data.get(i).substring(0, 4);
			String year2 = data.get(i+1).substring(0, 4);
			String monthDay1 = data.get(i).substring(4, 8);
			String monthDay2 = data.get(i+1).substring(4, 8);
			curDay = Long.valueOf(data.get(i));
			nextDay = Long.valueOf(data.get(i+1));
			if(year1.equals(year2)){
				if(((curDay+1) == nextDay)){//同一年的周处理
					end = i+1;
					continue;
				}else{
					System.out.println(start+","+end);
					if((end-start+1)>max){ 
						max = end-start+1;
					}
					start = end = i+1; 
				}
			}else{
				if((curDay+1) != nextDay){
					//最后一天处理
					if("1231".equals(monthDay1)
						&&((Long.valueOf(year1)+1)==Long.valueOf(year2))
							&&monthDay2.equals("0101")){
						end = i+1;
						continue;
					}else{//nextday=00000000
						System.out.println(start+","+end);
						if((end-start+1)>max){ 
							max = end-start+1;
						}
						start = end = i+1; 
					}
				}
			}
			  nextDay = 0l;
			  curDay = 0l;
		}
		 System.out.println();
		 System.out.println("max="+max);
		 return max;
	}
	public static int weeks(){
		//分组求出该成员每年的最大周周数  如201552
		List<String> data = new ArrayList<String>();
		data.add("201401");
		data.add("201402");
		data.add("201403");
		data.add("201404");
		data.add("201452");
		data.add("201501");
		data.add("201524");
		data.add("201525");
		data.add("201528");
		data.add("201530");
		data.add("201552");
		data.add("201601");
		data.add("00000000");
		Map<String,String> maxWeek = new HashMap<String, String>();
		maxWeek.put("2014", "201452");
		maxWeek.put("2015", "201552");
	    long nextDay = 0;
	    long curDay = 0;
	    String sixZERO = "000000";
	    Map<String,List<String>> records = new HashMap<String,List<String>>();
	    int start = 0,end = 0;
	    for(int i=0 ;i<data.size();i++){
	    	System.out.print("data["+i+"]="+data.get(i)+",");
	    }System.out.println();
	    int max = 0;
		for(int i=0 ;i<data.size()-1;i++){
			String year1 = data.get(i).substring(0, 4);
			String year2 = data.get(i+1).substring(0, 4);
			String week1 = data.get(i).substring(4, 6);
			String week2 = data.get(i+1).substring(4, 6);
			curDay = Long.valueOf(data.get(i));
			nextDay = Long.valueOf(data.get(i+1));
			if(year1.equals(year2)){
				if(((curDay+1) == nextDay)){//同一年的周处理
					end = i+1;
					continue;
				}else{
					System.out.println(start+","+end);
					if((end-start+1)>max){ 
						max = end-start+1;
					}
					start = end = i+1; 
				}
			}else{
				if((curDay+1) != nextDay){
					//最后一周处理
					if(data.get(i).equals(maxWeek.get(year1))
						&&((Long.valueOf(year1)+1)==Long.valueOf(year2))
							&&week2.equals("01")){
						end = i+1;
						System.out.println(start+","+end);
						continue;
					}
				}else{
					System.out.println(start+","+end);
					if((end-start+1)>max){ 
						max = end-start+1;
					}
					start = end = i+1; 
				}
			}
			  nextDay = 0;
			  curDay = 0;
		}
		 System.out.println("\nmax="+max);
		 return max;
	}
	public static int months(){
		List<String> data = new ArrayList<String>();
		data.add("201401");
		data.add("201402");
		data.add("201403");
		data.add("201407");
		data.add("201412");
		data.add("201501");
		data.add("201503");
		data.add("201504");
		data.add("201505");
		data.add("201506");
		data.add("201512");
		data.add("201601");
		data.add("00000000");
	    Long nextDay = 0l;
	    Long curDay = 0l;
	    String sixZERO = "000000";
	    Map<String,List<String>> records = new HashMap<String,List<String>>();
	    int start = 0,end = 0;
	    for(int i=0 ;i<data.size();i++){
	    	System.out.print("data["+i+"]="+data.get(i)+",");
	    }
	    int max = 0;
	    System.out.println("\n");
		for(int i=0 ;i<data.size()-1;i++){
			String year1 = data.get(i).substring(0, 4);
			String year2 = data.get(i+1).substring(0, 4);
			String month1 = data.get(i).substring(4, 6);
			String month2 = data.get(i+1).substring(4, 6);
			curDay = Long.valueOf(data.get(i));
			nextDay = Long.valueOf(data.get(i+1));
			if(year1.equals(year2)){
				if(((curDay+1) == nextDay)){//同一年的月处理
					end = i+1;
					continue;
				}else{
					System.out.println(start+","+end);
					if((end-start+1)>max){ 
						max = end-start+1;
					}
					start = end = i+1; 
				}
			}else{
				if((curDay+1) != nextDay){//最后一月处理
					if(month1.equals("12")&&(Long.valueOf(year1)+1)==Long.valueOf(year2)
							&&month2.equals("01")){
						end = i+1;
						System.out.println(start+","+end);
						continue;
					}
				}else{
					System.out.println(start+","+end);
					if((end-start+1)>max){ 
						max = end-start+1;
					}
					start = end = i+1; 
				}
			}
			 nextDay = 0l;
			 curDay = 0l;
		}
		 System.out.println();
		 System.out.println("max="+max);
		 return max;
	}
	public static int seasons(){
		List<String> data = new ArrayList<String>();
		data.add("201401");
		data.add("201402");
		data.add("201404");
		data.add("201501");
		data.add("201502");
		data.add("201503");
		data.add("201601");
		data.add("00000000");
	    String sixZERO = "000000";
	    Long nextDay = 0l;
	    Long curDay = 0l;
	    Map<String,List<String>> records = new HashMap<String,List<String>>();
	    int start = 0,end = 0;
	    for(int i=0 ;i<data.size();i++){
	    	System.out.print("data["+i+"]="+data.get(i)+",");
	    }
	    int max = 0;
	    System.out.println("\n");
		for(int i=0 ;i<data.size()-1;i++){
			String year1 = data.get(i).substring(0, 4);
			String year2 = data.get(i+1).substring(0, 4);
			String season1 = data.get(i).substring(4, 6);
			String season2 = data.get(i+1).substring(4, 6);
			curDay = Long.valueOf(data.get(i));
			nextDay = Long.valueOf(data.get(i+1));
			if(year1.equals(year2)){//同一年
				if(((curDay+1) == nextDay)){//季度处理
					end = i+1;
					continue;
				}else{
					System.out.println(start+","+end);
					if((end-start+1)>max){ 
						max = end-start+1;
					}
					start = end = i+1; 
				}
			}else{//不同年
				if((curDay+1) != nextDay){
					//最后一月处理
					if(season1.equals("04")&&(Long.valueOf(year1)+1)==Long.valueOf(year2)
							&&season2.equals("01")){
						end = i+1;
						continue;
					}else{
						System.out.println(start+","+end);
						if((end-start+1)>max){ 
							max = end-start+1;
						}
						start = end = i+1; 
				}
			  }
			}
			  nextDay = 0l;
			  curDay = 0l;
		}
		 System.out.println("\nmax="+max);
		 return max;
	}
	
	public static Integer cal(){
		List<Integer> data = new ArrayList<Integer>();
		data.add(1);data.add(1);data.add(0);data.add(-1);
		data.add(1);data.add(1);data.add(1);data.add(1);
		data.add(-1);data.add(1);data.add(1);data.add(1);data.add(1);data.add(1);data.add(1);
		int start =0 ;
		int end = 0;
		int max = 0 ;
		List<Integer> lst = new ArrayList<Integer>();
		int i =0;
		for(;i<data.size();i++){
			if(data.get(i)>0){
				end ++;
				lst.add(i);
			}else{
				System.out.println(lst);
				if(max<(end-start+1)){
					max = end-start;
				}
				start = end ;
				lst = new ArrayList<Integer>();
			}
		}
		if(i==data.size()&&lst.size()>0){
			System.out.println(lst);
			if(max<(end-start)){
				max = end-start;
			}
		}
		return max;
	}
	 public static void main(String[] args) {
		 System.out.println("20150712".substring(0,6)+"01");
		 Server server;
		try {
			server = Server.createTcpServer();	
			server.start();
//			关闭数据库服务
			server.stop();
//			.数据库注销
			server.shutdown();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	 }
}
