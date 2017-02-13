package com.cyb.qutoes.service;

import java.util.List;
import java.util.Map;

public interface GrabDataService {
	public void saveCodeInfor();
	public List getAllCodeInfor();
	public void persisMinQutoes(List<Map<String, Object>> data);
	public List getAllDayQutoes(String stockCode);
	public void grabQutoes();
}
