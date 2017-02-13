package com.cyb.dao;

import java.util.List;

import javax.annotation.Resource;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;

import com.cyb.vo.Stock;

@Repository("stockDao")
public class StockDaoImpl {
	@Resource(name="sessionFactory")
	 public SessionFactory sessionFactory ;
	 public Session getSession(){
			Session session = null ;
			try {
				session = sessionFactory.getCurrentSession();
				if(session==null){
					session = sessionFactory.openSession();
				}
			} catch (Exception e) {
				System.out.println("获取session异常！");
				return sessionFactory.openSession();
			}
			return session;
		}
	 public void saveStock(Stock stock){
		 this.getSession().save(stock);
	 }
	 public void saveStockList(List<Stock> stocks){
		 if(stocks!=null){
				for(Stock stock:stocks){
					saveStock(stock);
				}
		 }
	 }
}
