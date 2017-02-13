package com.cyb.mbean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import net.sf.json.JSONArray;

import com.cyb.mbean.utils.GrabQutoesThread;
import com.cyb.page.Pagination;
import com.cyb.push.PushConstant;
import com.cyb.qutoes.dao.impl.GrabDataDaoImpl;
import com.cyb.qutoes.utils.GrabEntity;
import com.cyb.qutoes.utils.GrabSHStockCodeThread;
import com.cyb.qutoes.utils.GrabSZStockCodeThread;
import com.cyb.utils.FileUtils;
import com.cyb.utils.SpringUtil;
/**
 * SELECT code_ ,count(*) FROM STOCK group by code_ having  count(*)>1
 * @author DHUser
 *
 */
public class GrabQutoesBean {
	public static boolean start = false;
	public String  查看需要推送的代码(){
		return PushConstant.qutoesCodes.keySet().toString();
	}
	public int 测试查询接口(String code,String jys){
		GrabDataDaoImpl dao = (GrabDataDaoImpl) SpringUtil.getBean("grabDataDao");
		return dao.query(code, jys);
	}
	public void 抓取行情() {
		new Thread(new GrabQutoesThread()).start();
	}
	public void 停止抓取(){
		start = false;
	}
    public List<Map<String, Object>> 上海股票代码(){
    	return GrabEntity.stocks1;
    }
    public void 并发获取深圳股票代码(int pageSize,int threads){
    	start = true;
    	ExecutorService executor = Executors.newFixedThreadPool(threads);
    	List<Future<List<Map<String, Object>>>> rs = new ArrayList<Future<List<Map<String,Object>>>>();
    	List<Map<String, Object>> stocks = Collections.synchronizedList(new ArrayList<Map<String,Object>>());
    	Pagination page = new Pagination(1,pageSize,999999);
    	for(int i=1;i<=page.getPageCount();i++){
    		Pagination page_ = new Pagination(i,pageSize,999999);
    		GrabSZStockCodeThread task = new GrabSZStockCodeThread(page_.getOffset(),(page_.getOffset()+pageSize));
    		Future<List<Map<String, Object>>> result = executor.submit(task);
    		rs.add(result);
    	}
    	for(int i=1;i<=rs.size();i++){
    		try {
				//stocks.addAll(rs.get(i).get());
    			List<Map<String, Object>>  t= rs.get(i).get();
    			if(t.size()>0){
					String res = JSONArray.fromObject(t).toString();
					FileUtils.writeString2File(res, "d:/stock/szstocks_"+i+".txt");
					System.out.println(i+"写结果："+res);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}
    	/*String res = JSONArray.fromObject(stocks).toString();
		FileUtils.writeString2File(res, GrabEntity.path2_1);
		System.out.println(res);*/
    	executor.shutdown();
    }
    public void 并发获取上海股票代码(int pageSize,int threads){
    	start = true;
    	ExecutorService executor = Executors.newFixedThreadPool(threads);
    	CompletionService<Integer> cs = new ExecutorCompletionService<Integer>(executor);
    	List<Future<List<Map<String, Object>>>> rs = new ArrayList<Future<List<Map<String,Object>>>>();
    	List<Map<String, Object>> stocks = Collections.synchronizedList(new ArrayList<Map<String,Object>>());
    	Pagination page = new Pagination(1,pageSize,999999);
    	for(int i=1;i<=page.getPageCount();i++){
    		Pagination page_ = new Pagination(i,pageSize,999999);
    		GrabSHStockCodeThread task = new GrabSHStockCodeThread(page_.getOffset(),(page_.getOffset()+pageSize));
    		Future<List<Map<String, Object>>> result = executor.submit(task);
    		rs.add(result);
    	}
    	for(int i=1;i<=rs.size();i++){
    		try {
				//stocks.addAll();
    			List<Map<String, Object>>  t= rs.get(i).get();
    			if(t.size()>0){
					String res = JSONArray.fromObject(t).toString();
					FileUtils.writeString2File(res, "d:/stock/shstocks_"+i+".txt");
					System.out.println(i+"写结果："+res);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			} catch (ExecutionException e) {
				e.printStackTrace();
			}
    	}
    	/*String res = JSONArray.fromObject(stocks).toString();
		FileUtils.writeString2File(res, GrabEntity.path1_1);
		System.out.println("结果："+res);*/
    	executor.shutdown();
    }
    public List<Map<String, Object>> 深圳股票代码(){
    	return GrabEntity.stocks2;
    }
	public void 获取上海股票代码() {
		GrabEntity.grabShStock();
	}
	public void 获取深圳股票代码() {
		GrabEntity.grabSzStock();
	}
	public void 显示日志(int flag) {
		if (flag == 1) {
			GrabQutoesThread.setStart(true);
		} else {
			GrabQutoesThread.setStart(false);
		}
	}
}
