package com.cyb.service;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.cyb.dao.StockDaoImpl;
import com.cyb.vo.Stock;
@Service("stockService")
public class StockServiceImpl {
	@Resource(name="stockDao")
	public StockDaoImpl stockDao ;
	public void saveStockList(List<Stock> stocks){
		this.stockDao.saveStockList(stocks);
	}
}
