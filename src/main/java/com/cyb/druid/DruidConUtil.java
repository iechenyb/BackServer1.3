package com.cyb.druid;
import java.sql.ResultSet;
import java.sql.Statement;

import com.alibaba.druid.pool.DruidDataSource;

public class DruidConUtil {
  public static void main(String[] args) {
	  try{
	 
	  DruidDataSource dataSource = new DruidDataSource(); 
	  dataSource.setDriverClassName("org.h2.Driver"); 
	  dataSource.setUsername("sa"); 
	  dataSource.setPassword("111111"); 
	  dataSource.setUrl("jdbc:h2:tcp://localhost/~/data/trade;AUTO_SERVER=true"); 
	  dataSource.setInitialSize(5); 
	  dataSource.setMinIdle(1); 
	  dataSource.setMaxActive(10); // 启用监控统计功能  
	  dataSource.setFilters("stat");// for mysql  
	  dataSource.setPoolPreparedStatements(false);
	  Statement stmt =  dataSource.getConnection().createStatement();
	  stmt.execute("select 1+1 from dual");
	  ResultSet rs = stmt.executeQuery("select 1+1 from dual");
	  rs.next();
	  System.out.println("value:"+rs.getInt(1));
	  }catch(Exception e){
		  e.printStackTrace();
	  }
}
}
