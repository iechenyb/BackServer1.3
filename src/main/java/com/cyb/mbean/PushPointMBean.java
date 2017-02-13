package com.cyb.mbean;

import com.cyb.push.PushPointThread;
import com.cyb.push.PushServer;
import com.cyb.utils.PropertyUtil;
/**
 * 推送服务
 * @author DHUser
 *
 */
public class PushPointMBean {
	public boolean 中文方法(){
		return true;
	}
    public boolean startPushServer(){
    	PushServer.startPushServer();
    	return true;
    }
    public String checkPushServer(){
    	if(PushServer.server!=null){
    		System.out.println("检查推送服务器状态：");
    		return PushServer.isStarted+":"+"推送地址："+PropertyUtil.get("puship")+":"+Integer.valueOf(PropertyUtil.get("pushport"));
    	}else{
    		return false+":"+"推送地址："+PropertyUtil.get("puship")+":"+Integer.valueOf(PropertyUtil.get("pushport"));
    	}
    }
    public boolean stopPushServer(){
    	PushServer.stopPushServer();
    	return true;
    }
    public boolean startPushPoint(){
    	PushPointThread pt = new PushPointThread(true);
    	new Thread(pt).start();
    	return true;
    }
    public boolean stopPushPoint(){
    	PushPointThread.start = false;
    	return true;
    }
}
