package com.cyb.listener;

import java.io.File;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.web.context.ContextLoader;

import com.cyb.cpu.CpuThread;
import com.cyb.h2.H2Manager;
import com.cyb.jms.spring.MessageReceiver;
import com.cyb.push.PushServer;
import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.qutoes.mapper.RealQutoesMapper;
import com.cyb.qutoes.quartz.MessageThread;
import com.cyb.qutoes.quartz.TestDeadLock;
import com.cyb.qutoes.utils.QutoesUtils;
import com.cyb.qutoes.utils.StockCodeInitEntity;
import com.cyb.service.StockServiceImpl;
import com.cyb.utils.Contants;
import com.cyb.utils.DateUtil;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.SpringUtil;
import com.cyb.vo.Stock;

public class CalculateCenterListener implements ServletContextListener {
	public static Log log = LogFactory.getLog(CalculateCenterListener.class);
	public JdbcTemplate  jdbcTemplate ; 
    public CalculateCenterListener() { 
    		 
    }
    public void contextInitialized(ServletContextEvent sce) {
    	SpringUtil.wac = ContextLoader.getCurrentWebApplicationContext();
    	this.jdbcTemplate = (JdbcTemplate) SpringUtil.getBean("jdbcTemplate");
    	GrabDataDao grabDataDao = (GrabDataDao) SpringUtil.getBean("grabDataDao");
    	JmsTemplate jmsTemplate = (JmsTemplate) SpringUtil.getBean("jmsTemplate");
		String webPath = sce.getServletContext().getRealPath("/");
		if(webPath.charAt(webPath.length()-1)!=File.separator.charAt(0)){
			webPath = webPath + File.separator;
		}
		ThreadPoolExecutor work = (ThreadPoolExecutor) Executors.newFixedThreadPool(10);
		Contants.WEBPATH = webPath;
		try {
			PropertyUtil.init("App");
			try {
				PushServer.startPushServer();
				initTableStructs();//初始化表结构
				initCodeNameMap();//初始化名称
				initCodeIndustryMap();//初始化行业分类
				OpenQutoesSwitch();//是否打开行情数据获取开关
				QutoesUtils.spiltTime("noon", 1);//初始化时间坐标
				initHolidays();
				initStocks();
				initRealQutoesToMem();
			} catch (Exception e2) {
				log.info(e2.toString());
				e2.printStackTrace();
			}
			/*try {
				CpuThread cpu = new CpuThread();
				new Thread(cpu).start();
			} catch (Exception e1) {
				log.info(e1.toString());
				e1.printStackTrace();
			}*/
			try {
				int numbers = Integer.valueOf(PropertyUtil.get("consumers"));
				for(int i=1;i<=numbers;i++){
					work.execute(new MessageReceiver(jmsTemplate,grabDataDao));
				}
			    //new Thread(new MessageReceiver(jmsTemplate,grabDataDao)).start();
			    //new Thread(new MessageReceiver(jmsTemplate,grabDataDao)).start();
				//new Thread(new QutoesFactory()).start();
			} catch (Exception e) {
				work.shutdown();
				log.info(e.toString());
				e.printStackTrace();
			}
			TestDeadLock.kaishi();
		} catch (Exception e) {
			e.printStackTrace();
			log.info("行情初始化时发生异常，请检查！"+e.toString());
		}
    }
    
    
    public void initRealQutoesToMem(){
    	try {
			QutoesContants.realQutoesMap = new HashMap<String,RealQutoes>();
			String sql = "SELECT * FROM REALTIMEQUTOES ";
			List<RealQutoes> lst = this.jdbcTemplate.query(sql, new RealQutoesMapper());
			if(lst!=null&&lst.size()>0){
				for(int i=0;i<lst.size();i++){
					QutoesContants.realQutoesMap.put(lst.get(i).getCode(),lst.get(i));
				}
			}
			log.info("实时行情信息 from mem："+QutoesContants.realQutoesMap.size());
		} catch (Exception e) {
			log.info(e.toString());
			e.printStackTrace();
		}
    }
    public void initStocks(){
    	if(Boolean.valueOf(PropertyUtil.get("enableInitStock"))){
		      StockServiceImpl stockService  = (StockServiceImpl) SpringUtil.getBean("stockService");
		      this.jdbcTemplate.execute("delete from stock where exchange_='sh'");	
		      this.jdbcTemplate.execute("delete from stock where exchange_='sz'");	
		      String root = PropertyUtil.getValueByKey("App", "stockFile");
		   	  String filePathSh = Contants.WEBPATH+root+File.separator+"sh"+File.separator;
		   	  String filePathSz = Contants.WEBPATH+root+File.separator+"sz"+File.separator;
		      StockCodeInitEntity entity  =new StockCodeInitEntity();
		  	  String fileName = "";//..\\SH-A.xls
		  	  entity.initIndustry();
		  	  List<Stock> stocks = null ;
		  	  for(int i=0;i<QutoesContants.INDUSTRYSORT.size();i++){
		  		  String industry = QutoesContants.INDUSTRYSORT.get(i).get("type");
		  		  fileName=filePathSh+"SH-"+QutoesContants.INDUSTRYSORT.get(i).get("type")+".xls";
		  		  stocks = entity.initSHStocks(fileName,industry);
		  		  stockService.saveStockList(stocks);
		  	  }
		  	 fileName = "";
		  	 for(int i=0;i<QutoesContants.INDUSTRYSORT.size();i++){
				  String industry = QutoesContants.INDUSTRYSORT.get(i).get("type");
				  fileName=filePathSz+"SZ-"+QutoesContants.INDUSTRYSORT.get(i).get("type")+".xls";
				  stocks = entity.initSZStocks(fileName,industry);
				  stockService.saveStockList(stocks);
			  }
		  	 this.jdbcTemplate.execute("delete STOCK where code_ is null ");
	         this.jdbcTemplate.execute("update STOCKCONFIG set curjyr=1 where alias ='INITSTOCKCODE'");
    	}
    }
    
    public void OpenQutoesSwitch(){
    	String day = DateUtil.date2long8(new Date()).toString(); //20150102
    	//09：:30-11:30 13:00-15:00
        Calendar openNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "start_noon"));
        Calendar closeNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "end_noon"));
        Calendar openAfterNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "start_afternoon"));
        Calendar closeAfterNoon = DateUtil.Calendar(day+PropertyUtil.getValueByKey("App", "end_afternoon"));
        Calendar curDate = DateUtil.Calendar(new Date());
       /* if((curDate.after(openNoon)&&curDate.before(closeNoon))||
           (curDate.after(openAfterNoon)&&curDate.before(closeAfterNoon))){
        	QutoesContants.SWTICH = true;
			MessageThread pushThread = new MessageThread();
			QutoesContants.pushPool.execute(pushThread);
        }else{
        	QutoesContants.SWTICH = false;
        }*/
    }
    
    public void initTableStructs(){
    	String sql = "SELECT count(*) FROM INFORMATION_SCHEMA.TABLES where TABLE_SCHEMA ='PUBLIC' ";
    	try {
			for(String tableName :QutoesContants.tables ){
				int count = this.jdbcTemplate.queryForInt(sql+" and table_name='"+tableName+"'");
				if(count==0){
					String createSql = QutoesContants.tableMaps.get(tableName);
					log.info("create table "+tableName);
					this.jdbcTemplate.execute(createSql);
				}
			}
			log.info("初始化数据表结构成功！");
		} catch (Exception e) {
			log.info("初始化数据表结构异常！");
			e.printStackTrace();
		}
    }
    public void initHolidays(){
    	List<String> holidays = this.jdbcTemplate.queryForList("SELECT JJR FROM HOLIDAYCONFIG ", String.class);
    	if(holidays!=null&&holidays.size()>0){
    		for(String holiday:holidays){
    			QutoesContants.holidays.put(holiday, holiday);
    		}
    	}		
    }
    public void initCodeNameMap(){
    	try {
			Contants.STOCKLIST = this.jdbcTemplate.queryForList("SELECT exCHANGE_ ||codE_  code,nvl(name_,'') name,industry FROM STOCK");
			Contants.STOCKMAP = new HashMap<String, Object>();
			QutoesContants.CODEINDUSTRY= new HashMap<String, String>();
			if(Contants.STOCKLIST!=null&&Contants.STOCKLIST.size()>0){
				for(Map tmp:Contants.STOCKLIST){
					Contants.STOCKMAP.put(tmp.get("CODE").toString(),tmp.get("NAME"));
				}
			}
//			log.info("股票代码中文名映射："+Contants.STOCKMAP);
			log.info("初始化股票代码中文名映射成功！");
		} catch (Exception e) {
			log.info("初始化股票代码中文名映射异常！");
			e.printStackTrace();
		}
    }
    public void initCodeIndustryMap(){
    	try {
			QutoesContants.CODEINDUSTRY= new HashMap<String, String>();
			if(Contants.STOCKLIST!=null&&Contants.STOCKLIST.size()>0){
				for(Map<String,Object> tmp:Contants.STOCKLIST){
					QutoesContants.CODEINDUSTRY.put(tmp.get("CODE").toString(), tmp.get("INDUSTRY").toString());
				}
			}
			log.info("初始化股票代码-行业映射成功！");
		} catch (Exception e) {
			log.info("初始化股票代码-行业映射异常！");
			e.printStackTrace();
		}
    }
	public void contextDestroyed(ServletContextEvent sce) {
		H2Manager.shutdown();
		PushServer.stopPushServer();
	}

}
