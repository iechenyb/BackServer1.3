package com.cyb.excel.ex.bean;

public class JX04 {

	public JX04(String data){
		String[] dt = data.split("#");
		this.xh = dt[0];
		this.jgdm=dt[1];
		this.jgmc=dt[2];
		this.bmmc=dt[3];
		this.gwmc=dt[4];
		this.khjlbh=dt[5];
		this.khjlxm=dt[6];
		this.cz=dt[7];
		this.sdye = Double.valueOf(dt[8]);
		this.rjye = Double.valueOf(dt[9]);
		this.clye = Double.valueOf(dt[10]);
		this.zjf = Double.valueOf(dt[11]);
		this.xzjf = Double.valueOf(dt[12]);
		this.cljf = Double.valueOf(dt[13]);
	}
	private String xh,tjrq,jgdm,jgmc,bmmc,gwmc,khjlbh,khjlxm,cz;
	private Double sdye,rjye,clye,zjf,xzjf,cljf;
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
	public void setCljf(Double cljf) {
		this.cljf = cljf;
	}

}
