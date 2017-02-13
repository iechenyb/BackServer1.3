package com.cyb.utils;

import org.springframework.web.context.WebApplicationContext;

public class SpringUtil {
	public static WebApplicationContext wac = null;//行情监听器初始化
	public static Object getBean(String beanName){
		if(wac!=null){
			return wac.getBean(beanName);
		}else{
			return new Object();
		}
	}
}
