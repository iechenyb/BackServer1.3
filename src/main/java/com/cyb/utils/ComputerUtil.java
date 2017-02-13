package com.cyb.utils;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class ComputerUtil {
	private static InetAddress addr;
	static {
		try {
			addr = InetAddress.getLocalHost();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}
	public static String getRealIP(){
		String ip=addr.getHostAddress().toString();//获得本机IP
		return ip;
	}
	public static String getDefaultIP(){
		return "127.0.0.1";
	}
	public static String getDefaultIPDomain(){
		return "localhost";
	}
	public static String getName(){
		String computerName=addr.getHostName().toString();//获得本机IP
		return computerName;
	}
	public static void main(String[] args) {
		System.out.println(ComputerUtil.getRealIP());
		System.out.println(ComputerUtil.getName());
	}
}
