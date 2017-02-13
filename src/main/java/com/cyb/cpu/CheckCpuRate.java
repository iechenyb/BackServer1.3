package com.cyb.cpu;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class CheckCpuRate implements Runnable{
static Log logger =LogFactory.getLog(CheckCpuRate.class);
  public void test(){
	  
  }
	public void run() {
		IMonitorService service = new MonitorServiceImpl();   
        MonitorInfoBean monitorInfo = null;
		try {
			monitorInfo = service.getMonitorInfoBean();
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.toString());
		}  
		while(true){
			logger.debug("cpu占有率=" + monitorInfo.getCpuRatio()); 
			double rate = monitorInfo.getCpuRatio();
			if(rate>=98.0){
				logger.debug("System is over,because cpu is used to "+rate+" %");
			}
		}
	}
	 
 }