package com.cyb.cpu;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import net.sf.json.JSONArray;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.cyb.push.PushServer;
import com.cyb.utils.DateUtil;


public class CpuThread  implements Runnable{
	Logger log = LoggerFactory.getLogger(CpuThread.class);
	static int maxPoints = 10000;
	static int windowWidth = 500;
	public void run() {
		Thread.currentThread().setName("CPU监控线程");
		while (true) {
			try {
				if(MonitorServiceImpl.timeArr.size()>maxPoints){
					MonitorServiceImpl.timeArr.clear();
					MonitorServiceImpl.rateArr.clear();
					log.debug("CPU占用信息超过"+maxPoints+"，数据已经清除！");
				}
				MonitorServiceImpl.initCpuData();
				Random random = new Random();
				Map<String, Object> map = new HashMap<String, Object>();
				map.put("x", random.nextInt(255));
				map.put("y", random.nextInt(255));
				map.put("z", random.nextInt(255));
				List<String> time =  new ArrayList<String>();
				List<String> data =  new ArrayList<String>();
				int total = MonitorServiceImpl.timeArr.size();
				double sum = 0;
				double max =0;
				double min = 0;
				double new1 = 0;
				if(total>windowWidth){
					int hh=total-windowWidth;
					time.addAll(MonitorServiceImpl.timeArr.subList(hh, total-1));
					data.addAll(MonitorServiceImpl.rateArr.subList(hh, total-1));
					max = min = Double.valueOf(data.get(0));
					new1=Double.valueOf(data.get(data.size()-1));
				}else if(total>0){
					time.addAll(MonitorServiceImpl.timeArr);
					data.addAll(MonitorServiceImpl.rateArr);
					max = min = Double.valueOf(data.get(0));
					new1=Double.valueOf(data.get(data.size()-1));
				}
				
				for(int i=0;i<data.size();i++){
					if(Double.valueOf(data.get(i))>max&&Double.valueOf(data.get(i))<=100){
						max = Double.valueOf(data.get(i));
					}
					if(Double.valueOf(data.get(i))<min&&Double.valueOf(data.get(i))>0){
						min = Double.valueOf(data.get(i));
					}
					sum = sum+Double.valueOf(data.get(i));
				}
				
				if(data.size()>0){
					double avg = sum/data.size();
					map.put("avg",avg);
				}else{
					map.put("avg",0);
				}
				if(total>0){
					map.put("start",MonitorServiceImpl.timeArr.get(0));
					map.put("end",MonitorServiceImpl.timeArr.get(total-1));
				}
				map.put("max",max);map.put("min",min);map.put("new1",new1);
				map.put("points",MonitorServiceImpl.timeArr.size());
				map.put("cur",DateUtil.format(new Date(), "yyyy-MM-dd HH:mm:ss.SSS"));
				map.put("time", JSONArray.fromObject(time));
				map.put("data", JSONArray.fromObject(data));
				map.put("width",windowWidth);
				try {
					PushServer.sendBroadcast(map);
					log.debug("push CPU占用信息:"+map);
					Thread.sleep(1);
				} catch (Exception e) {
					e.printStackTrace();
					log.error("push ocur exception don't care...."+e.toString());
				}//广播推送消息
			} catch (Exception e) {
				log.error("采集cpu数据出现问题，可忽略...."+e.toString());
			}		
		}
	}
}
