package com.cyb.qutoes;

public class MessageEntity {
	private String code = "";
	private String exchage = "";
	private String type = "";

	public MessageEntity(String code, String exchange, String type) {
		this.code = code;
		this.exchage = exchange;
		this.type = type;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getExchage() {
		return exchage;
	}

	public void setExchage(String exchage) {
		this.exchage = exchage;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
   public String toString(){
	   return this.code+","+this.exchage+","+this.type;
   }
}
