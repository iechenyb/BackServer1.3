package com.cyb.qutoes.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import com.cyb.mbean.GrabQutoesBean;
import com.cyb.qutoes.dao.impl.GrabDataDaoImpl;
import com.cyb.utils.SpringUtil;

public class GrabSHStockCodeThread implements Callable<List<Map<String,Object>>>{
	private int s;
	private int e;
	public GrabSHStockCodeThread(int s,int e){
		this.s = s;
		this.e = e;
	}
	 private ThreadLocal<Integer> threadLocal =   new ThreadLocal<Integer>();
    public  List<Map<String,Object>> run(){
    	GrabDataDaoImpl dao = (GrabDataDaoImpl) SpringUtil.getBean("grabDataDao");
    	List<Map<String,Object>> list = Collections.synchronizedList(new ArrayList<Map<String,Object>>());//new ArrayList<Map<String,Object>>();
    	String str = "http://hq.sinajs.cn/list=sh";
		Map<String,Object> tmp =null;
    	for(int i=s;i<=e;i++){
    		if(GrabQutoesBean.start){
			String stri =String.valueOf(i) ;
			String param = GrabEntity.getZeroStr(6-stri.length())+stri;
			//System.out.println(str+param);
		    String data = GrabEntity.grabJsonDataFromURL(str+param);
		    data  = data.replace(";", "");
		    try{
		    String key = data.split("=")[0].substring(data.split("=")[0].length()-8, data.split("=")[0].length());
			String name = new String(data.split("=")[1].split(",")[0]);
			if(!name.equals("\"\"")&&!name.equals("")){//StringUtils.isNotEmpty(name)
			    tmp = new HashMap<String,Object>();
				name = name.substring(1);
				tmp.put("code", key.substring(2, key.length()));
				tmp.put("jys", key.substring(0,2));
				tmp.put("name", name);
				threadLocal.set(dao.query(tmp.get("code").toString(), tmp.get("jys").toString()));
				if(threadLocal.get()<=0){
					dao.insertStock(tmp.get("code").toString(), tmp.get("jys").toString(), name);
				}
				list.add(tmp);
			}	
			}catch(Exception e){
				System.out.println("错误的代码信息：["+data+"]:"+e.toString());
			}
    		}
    	}
    	return list;
    }
	@Override
	public List<Map<String,Object>> call() throws Exception {
		Thread.currentThread().setName("shstock["+s+"-"+e+"]");
		return run();
	}
}