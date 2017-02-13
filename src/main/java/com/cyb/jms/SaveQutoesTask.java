package com.cyb.jms;

import java.util.concurrent.Callable;

import javax.jms.MapMessage;

import org.apache.log4j.Logger;

import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.mapper.RealQutoesMapper;

public class SaveQutoesTask implements Callable<Boolean> {
	public static Logger log = Logger.getLogger(MessageReceiver.class);
	public GrabDataDao dao;
	public MapMessage message;
	public SaveQutoesTask(GrabDataDao dao,MapMessage message){
		this.message = message;
		this.dao = dao;
	}
	@Override
	public synchronized Boolean call() {
		try{
			final RealQutoes realQutoes = RealQutoesMapper.MapMessage2Object(message);
			if(!QutoesContants.realQutoesMap.containsKey(realQutoes.getCode())){
				dao.saveRealQutoes(realQutoes);
				QutoesContants.realQutoesMap.put(message.getString("code"), realQutoes);
			}else{
				dao.updateRealQutoes(realQutoes);
			}
		}catch(Exception e){
			log.error(e.toString());
			e.printStackTrace();
			return false;
		}
		return true;
	}

}
