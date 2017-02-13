package com.cyb.qutoes.bean;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class KPoint implements RowMapper<KPoint>{
  public String record_date;
  public Double open=0.0;
  public Double high=0.0;
  public Double low=0.0;
  public Double price=0.0;
  public Double getPreClose() {
	return preClose;
}
public void setPreClose(Double preClose) {
	this.preClose = preClose;
}
public Double preClose=0.0;
	@Override
	public KPoint mapRow(ResultSet rs, int arg1) throws SQLException {
		KPoint k = new KPoint();
		k.setRecord_date(rs.getString("RECORD_DATE_"));
		k.setHigh(rs.getDouble("HIGH_"));
		k.setOpen(rs.getDouble("OPEN_"));
		k.setLow(rs.getDouble("LOW_"));
		k.setPrice(rs.getDouble("PRICE_"));
		k.setPreClose(rs.getDouble("PRECLOSE_"));
		return k;
	}
	public String getRecord_date() {
		return record_date;
	}
	public void setRecord_date(String record_date) {
		this.record_date = record_date;
	}
	public Double getOpen() {
		return open;
	}
	public void setOpen(Double open) {
		this.open = open;
	}
	public Double getHigh() {
		return high;
	}
	public void setHigh(Double high) {
		this.high = high;
	}
	public Double getLow() {
		return low;
	}
	public void setLow(Double low) {
		this.low = low;
	}
	public Double getPrice() {
		return price;
	}
	public void setPrice(Double price) {
		this.price = price;
	}
}
