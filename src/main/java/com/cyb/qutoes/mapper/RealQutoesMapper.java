package com.cyb.qutoes.mapper;

import java.io.Serializable;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.jms.JMSException;
import javax.jms.MapMessage;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.jdbc.core.RowMapper;

import com.cyb.qutoes.bean.RealQutoes;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.utils.Contants;
public class RealQutoesMapper implements RowMapper<RealQutoes>, Serializable{
	private static final long serialVersionUID = 1L;
	static Log log = LogFactory.getLog(RealQutoesMapper.class);
	@Override
	public RealQutoes mapRow(ResultSet rs, int arg1) throws SQLException {
		    RealQutoes obj = new RealQutoes();
		    obj.setId(rs.getString("ID_"));
		    obj.setName(rs.getString("NAME_"));
		    obj.setCode(rs.getString("CODE_"));
		    obj.setClose(rs.getString("CLOSE_"));
		    obj.setHigh(rs.getString("HIGH_"));
		    obj.setLow(rs.getString("LOW_"));
		    obj.setVolume(rs.getString("VOLUME"));
		    obj.setTurnvolume(rs.getString("TURNVOLUME"));
		    obj.setOpen(rs.getString("OPEN_"));
		    obj.setPreClose(rs.getString("PRECLOSE_"));
		    obj.setPrice(rs.getString("PRICE_"));
		    obj.setDay(rs.getString("DAY_"));
		    obj.setTime(rs.getString("TIME_"));
		    obj.setIndustry(rs.getString("INDUSTRY"));
		    obj.setZdbz(rs.getString("ZDBZ"));
		    
		return obj;
	}
	public static RealQutoes MapMessage2Object(MapMessage msg){		
		RealQutoes obj = new RealQutoes();
		String qutoes ="";
		try {
			qutoes = msg.getString("qutoes");
			String[] dataArr = qutoes.split(",");
			obj = new RealQutoes();
			obj.setCode(msg.getString("code"));
			obj.setName(dataArr[Contants.NAME]);
			obj.setOpen(dataArr[Contants.OPEN]);
			obj.setPreClose(dataArr[Contants.PRECLOSE]);
			obj.setHigh(dataArr[Contants.HIGH]);
			obj.setLow(dataArr[Contants.LOW]);
			obj.setClose(dataArr[Contants.PRICE]);
			obj.setPrice(dataArr[Contants.PRICE]);
			obj.setDay(dataArr[Contants.DAY]);
			obj.setTime(dataArr[Contants.TIME]);
			obj.setVolume(dataArr[Contants.COLUMNCASH]);
			obj.setTurnvolume(dataArr[Contants.TURNVOLUME]);
			obj.setIndustry(QutoesContants.CODEINDUSTRY.get(obj.getCode()));
			double preclose = Double.valueOf(obj.getPreClose());
			double price = Double.valueOf(obj.getPrice());
			double open = Double.valueOf(obj.getOpen());
			double A = 100*(price-preclose)/preclose;
			int zdbz = 0;//平盘
			if(open>0){
				if(A>=9){
					zdbz=1;
				}else if(A>0&&A<9){//上涨
					zdbz=3;
				}
				if(A<=-9){
					zdbz=2;
				}else if(A>-9&&A<0){
					zdbz=4;//下跌
				}								
			}else if(open==0){
				zdbz=5;//停牌
				price = preclose;
			}
			obj.setZdbz(String.valueOf(zdbz));
		} catch (Exception e) {
			log.error(qutoes+"->"+e.toString());
			//e.printStackTrace();
		}
		//obj.setOper_time();
		return obj;
	}
	public static RealQutoes String2Object(String code,String msg){		
		RealQutoes obj = new RealQutoes();
		String qutoes ="";
		try {
			String[] dataArr = msg.replaceAll("\"", "").replaceAll(";", "").split(",");
			obj = new RealQutoes();
			obj.setCode(code);
			obj.setName(dataArr[Contants.NAME]);
			obj.setOpen(dataArr[Contants.OPEN]);
			obj.setPreClose(dataArr[Contants.PRECLOSE]);
			obj.setHigh(dataArr[Contants.HIGH]);
			obj.setLow(dataArr[Contants.LOW]);
			obj.setClose(dataArr[Contants.PRICE]);
			obj.setPrice(dataArr[Contants.PRICE]);
			obj.setDay(dataArr[Contants.DAY]);
			obj.setTime(dataArr[Contants.TIME]);
			obj.setVolume(dataArr[Contants.COLUMNCASH]);
			obj.setTurnvolume(dataArr[Contants.TURNVOLUME]);
			obj.setIndustry(QutoesContants.CODEINDUSTRY.get(obj.getCode()));
			double preclose = Double.valueOf(obj.getPreClose());
			double price = Double.valueOf(obj.getPrice());
			double open = Double.valueOf(obj.getOpen());
			double A = 100*(price-preclose)/preclose;
			int zdbz = 0;//平盘
			if(open>0){
				if(A>=9){
					zdbz=1;
				}else if(A>0&&A<9){//上涨
					zdbz=3;
				}
				if(A<=-9){
					zdbz=2;
				}else if(A>-9&&A<0){
					zdbz=4;//下跌
				}								
			}else if(open==0){
				zdbz=5;//停牌
				price = preclose;
			}
			obj.setZdbz(String.valueOf(zdbz));
		} catch (Exception e) {
			log.error(qutoes+"->"+e.toString());
			//e.printStackTrace();
		}
		//obj.setOper_time();
		return obj;
	}
	public static RealQutoes String2ObjectHK(String code,String msg){		
		RealQutoes obj = new RealQutoes();
		String qutoes ="";
		try {
			String[] dataArr = msg.replaceAll("\"", "").replaceAll(";", "").split(",");
			obj = new RealQutoes();
			obj.setCode(code);
			obj.setName(dataArr[Contants.ZNNAME_].replace("'", " "));
			obj.setOpen(dataArr[Contants.OPEN_]);
			obj.setPreClose(dataArr[Contants.PRECLOSE_]);
			obj.setHigh(dataArr[Contants.HIGH_]);
			obj.setLow(dataArr[Contants.LOW_]);
			obj.setClose(dataArr[Contants.PRICE_]);
			obj.setPrice(dataArr[Contants.PRICE_]);
			obj.setDay(dataArr[Contants.DAY_]);
			obj.setTime(dataArr[Contants.TIME_]);
			obj.setVolume(dataArr[Contants.CJE]);
			obj.setTurnvolume(dataArr[Contants.CJL]);
			obj.setIndustry("hk");
			double preclose = Double.valueOf(obj.getPreClose());
			double price = Double.valueOf(obj.getPrice());
			double open = Double.valueOf(obj.getOpen());
			double A = 100*(price-preclose)/preclose;
			int zdbz = 0;//平盘
			if(open>0){
				if(A>=9){
					zdbz=1;
				}else if(A>0&&A<9){//上涨
					zdbz=3;
				}
				if(A<=-9){
					zdbz=2;
				}else if(A>-9&&A<0){
					zdbz=4;//下跌
				}								
			}else if(open==0){
				zdbz=5;//停牌
				price = preclose;
			}
			obj.setZdbz(String.valueOf(zdbz));
		} catch (Exception e) {
			log.error(qutoes+"->"+e.toString());
			e.printStackTrace();
		}
		//obj.setOper_time();
		return obj;
	}
}
