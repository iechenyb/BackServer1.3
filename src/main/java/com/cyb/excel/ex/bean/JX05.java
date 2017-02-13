package com.cyb.excel.ex.bean;

public class JX05 {
	public JX05(String data){
		String[] dt = data.split("#");
		this.xh = dt[0];
		this.tjrq = dt[1];
		this.jgdm=dt[2];
		this.jgmc=dt[3];
		this.bmmc=dt[4];
		this.gwmc=dt[5];
		this.khjlbh=dt[6];
		this.khjlxm=dt[7];
		this.cz=dt[8];
		this.sdye = Double.valueOf(dt[9]);
		this.rjye = Double.valueOf(dt[10]);
		this.clye = Double.valueOf(dt[11]);
		this.zjf = Double.valueOf(dt[12]);
		this.xzjf = Double.valueOf(dt[13]);
		this.cljf = Double.valueOf(dt[14]);
	}
	private String xh,tjrq,jgdm,jgmc,bmmc,gwmc,khjlbh,khjlxm,cz;
	private Double sdye=0.0,rjye=0.0,clye=0.0,zjf=0.0,xzjf=0.0,cljf=0.0,dyjx=0.0,curjx=0.0;
	public String getTjrq() {
		return tjrq;
	}
	public void setTjrq(String tjrq) {
		this.tjrq = tjrq;
	}
	public String getJgdm() {
		return jgdm;
	}
	public void setJgdm(String jgdm) {
		this.jgdm = jgdm;
	}
	public String getJgmc() {
		return jgmc;
	}
	public void setJgmc(String jgmc) {
		this.jgmc = jgmc;
	}
	public String getBmmc() {
		return bmmc;
	}
	public void setBmmc(String bmmc) {
		this.bmmc = bmmc;
	}
	public String getGwmc() {
		return gwmc;
	}
	public void setGwmc(String gwmc) {
		this.gwmc = gwmc;
	}
	public String getKhjlbh() {
		return khjlbh;
	}
	public void setKhjlbh(String khjlbh) {
		this.khjlbh = khjlbh;
	}
	public String getKhjlxm() {
		return khjlxm;
	}
	public void setKhjlxm(String khjlxm) {
		this.khjlxm = khjlxm;
	}
	public String getXh() {
		return xh;
	}
	public void setXh(String xh) {
		this.xh = xh;
	}
	public String getCz() {
		return cz;
	}
	public void setCz(String cz) {
		this.cz = cz;
	}
	public Double getSdye() {
		return sdye;
	}
	public void setSdye(Double sdye) {
		this.sdye = sdye;
	}
	public Double getRjye() {
		return rjye;
	}
	public void setRjye(Double rjye) {
		this.rjye = rjye;
	}
	public Double getClye() {
		return clye;
	}
	public void setClye(Double clye) {
		this.clye = clye;
	}
	public Double getZjf() {
		return zjf;
	}
	public void setZjf(Double zjf) {
		this.zjf = zjf;
	}
	public Double getXzjf() {
		return xzjf;
	}
	public void setXzjf(Double xzjf) {
		this.xzjf = xzjf;
	}
	public Double getCljf() {
		return cljf;
	}
	public Double getCurjx() {
		return curjx;
	}
	public void setCurjx(Double curjx) {
		this.curjx = curjx;
	}
	public Double getDyjx() {
		return dyjx;
	}
	public void setDyjx(Double dyjx) {
		this.dyjx = dyjx;
	}
	public void setCljf(Double cljf) {
		this.cljf = cljf;
	}
}
