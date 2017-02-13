package com.cyb.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegxUtils {
 public static String getHanzi(String source){
	 String regex="([\u4e00-\u9fa5]+)";
	 String ret = "";
	 Matcher matcher = Pattern.compile(regex).matcher(source);
	 if(matcher.find()){
	 	ret = matcher.group(0);
	 }
	 return ret;
 }
 public static String getZimu(String source){
	 String regex="([a-zA-Z]+)";
	 String ret = "";
	 Matcher matcher = Pattern.compile(regex).matcher(source);
	 if(matcher.find()){
	 	ret = matcher.group(0);
	 }
	 return ret;
 }
 public static String getShuzi(String source){
	 String regex="([0-9]+)";
	 String ret = "";
	 Matcher matcher = Pattern.compile(regex).matcher(source);
	 if(matcher.find()){
	 	ret = matcher.group(0);
	 }
	 return ret;
 }
}
