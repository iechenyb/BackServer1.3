package com.cyb.mbean;

import com.cyb.utils.PropertyUtil;

public class MyMBean1 {
 public void 重置配置文件属性值(String key,String value){
	 PropertyUtil.resetValue(key, value);
 }
}
