package com.cyb.push;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;

import com.cyb.h2.H2Manager;
import com.cyb.qutoes.dao.GrabDataDao;
import com.cyb.utils.SpringUtil;



/**
 * Application Lifecycle Listener implementation class PushServerListener
 *
 */
public class PushServerListener implements ServletContextListener {
	public static Logger log = LoggerFactory.getLogger(PushServerListener.class);
    public PushServerListener() {
    }

    public void contextDestroyed(ServletContextEvent arg0) {
    }

    public void contextInitialized(ServletContextEvent arg0) {
    	
    	try {
    		H2Manager.initServer();
    		H2Manager.start();
    		/*WebApplicationContext wac = ContextLoader.getCurrentWebApplicationContext();
    		JdbcTemplate h2;
			JdbcTemplate mysql;
			SpringUtil.wac = ContextLoader.getCurrentWebApplicationContext();
			GrabDataDao grabDataDao = (GrabDataDao) SpringUtil.getBean("grabDataDao");
			try {
				h2 = (JdbcTemplate) wac.getBean("jdbcTemplate");
				log.info("h2 query test ret=1?:ret="+h2.queryForInt("select 1 from dual"));
				mysql = (JdbcTemplate) wac.getBean("jdbcTemplate1");
				log.info("mysql query test ret=2?:ret="+mysql.queryForInt("select 2 from dual"));
			} catch (Exception e) {
				log.info("多数据源查询测试失败！"+e.toString());
			}
    		
			log.info("***************************");
    		new PushServer().startPushServer();
    		PushThread thread = new PushThread(grabDataDao);
    		Thread push = new Thread(thread);
    		push.start();
    		log.info("***************************");*/
    		
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
	
}
