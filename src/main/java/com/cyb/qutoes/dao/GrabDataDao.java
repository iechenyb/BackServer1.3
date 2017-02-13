package com.cyb.qutoes.dao;

import java.util.List;
import java.util.Map;

import com.cyb.qutoes.bean.RealQutoes;

public interface GrabDataDao {
   public void saveCodeInfor();
   public List getAllCodeInfor();
   public void persisMinQutoesBatch(List<Map<String, Object>> data);
   public List getAllDayQutoes(String stockCode);
   public void persisCloseQutoes(String stockCode);
   public void test(String stockCode);
   public void persisCloseQutoesIndicator(String stockCode);
   public List<Map<String, Object>> staticsCompany();
   public Map<String,Object> hsindiator();
   public Map<String,Object> lineJson(String code);
   public void copyRealQutoesToMinqutoes();
   public String updateRealQutoes(RealQutoes realQutoes);
   public String saveRealQutoes(RealQutoes realQutoes);
   public void exeSqls(List<String> sqls);
   public Map<String,Object>  myConcernJson(String userid);
   public Map<String,Object> kJson(String code);
   public void exeSql(String sql);
   public int updateRealtimeBatch(List<RealQutoes> list) ;
   public int insertRealtimeBatch(List<RealQutoes> list);
}
