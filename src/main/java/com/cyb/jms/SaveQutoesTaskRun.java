package com.cyb.jms;

import java.util.concurrent.Callable;

import javax.jms.MapMessage;

import org.apache.log4j.Logger;

import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.mapper.RealQutoesMapper;

public class SaveQutoesTaskRun implements Runnable {
	public static Logger log = Logger.getLogger(MessageReceiver.class);
	public GrabDataDao dao;
	public MapMessage message;
	public SaveQutoesTaskRun(GrabDataDao dao,MapMessage message){
		this.message = message;
		this.dao = dao;
	}

	public void run() {
		try{
			//synchronized (dao) {
				RealQutoes realQutoes = RealQutoesMapper.MapMessage2Object(message);
				if(!QutoesContants.realQutoesMap.containsKey(realQutoes.getCode())){
					dao.saveRealQutoes(realQutoes);
					QutoesContants.realQutoesMap.put(message.getString("code"), realQutoes);
				}else{
					dao.updateRealQutoes(realQutoes);
				}
			//}
		}catch(Exception e){
			log.error(e.toString());
			e.printStackTrace();
		}
	}

	/*@Override
	public void run() {
		
	}*/

}
