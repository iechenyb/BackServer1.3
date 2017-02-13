package com.cyb.utils;

import java.text.DecimalFormat;

public class DataUtils {
	public static String roundStr(Double number,int prec){
		if(number==0){return "0";}
		DecimalFormat df = new DecimalFormat(pattern(prec));
		return df.format(number);
	}
	public static double roundDouble(double number,int prec){
		return Double.valueOf(roundStr(number,prec));
	}
	public static String pattern(int len){
		if(len==0){return "0";}
		String ret = "0.";
		for(int i=0;i<len;i++){
			ret+="0";
		}
		return ret;
	}
public static void main(String[] args) {
	 /*System.out.println(DataUtils.round(1.2233665585, 2));
	 System.out.println(DataUtils.round(1.2233665585, 5));
	 System.out.println(DataUtils.round(0.2233665585, 5));
	 System.out.println(DataUtils.round(2.1707714e1, 3));*/
   /*
   double i=2.1, j=2.1, k=2.5, m=2.9; 
   System.out.println("舍掉小数取整:Math.floor(2)=" + (int)Math.floor(i)); 
   System.out.println("舍掉小数取整:Math.floor(2.1)=" + (int)Math.floor(j)); 
   System.out.println("舍掉小数取整:Math.floor(2.5)=" + (int)Math.floor(k)); 
   System.out.println("舍掉小数取整:Math.floor(2.9)=" + (int)Math.floor(m)); 
   System.out.println("四舍五入取整:(2)=" + new BigDecimal("2").setScale(0, BigDecimal.ROUND_HALF_UP)); 
   System.out.println("四舍五入取整:(2.1)=" + new BigDecimal("2.1").setScale(0, BigDecimal.ROUND_HALF_UP)); 
   System.out.println("四舍五入取整:(2.5)=" + new BigDecimal("2.5").setScale(0, BigDecimal.ROUND_HALF_UP)); 
   System.out.println("四舍五入取整:(2.9)=" + new BigDecimal("2.9").setScale(0, BigDecimal.ROUND_HALF_UP));

   System.out.println("凑整:Math.ceil(2)=" + (int)Math.ceil(i)); 
   System.out.println("凑整:Math.ceil(2.1)=" + (int)Math.ceil(j)); 
   System.out.println("凑整:Math.ceil(2.5)=" + (int)Math.ceil(k)); 
   System.out.println("凑整:Math.ceil(2.9)=" + (int)Math.ceil(m));

   System.out.println("舍掉小数取整:Math.floor(-2)=" + (int)Math.floor(-i)); 
   System.out.println("舍掉小数取整:Math.floor(-2.1)=" + (int)Math.floor(-j)); 
   System.out.println("舍掉小数取整:Math.floor(-2.5)=" + (int)Math.floor(-k)); 
   System.out.println("舍掉小数取整:Math.floor(-2.9)=" + (int)Math.floor(-m)); 
   
   System.out.println("四舍五入取整:(-2)=" + new BigDecimal("-2").setScale(0, BigDecimal.ROUND_HALF_UP)); 
   System.out.println("四舍五入取整:(-2.1)=" + new BigDecimal("-2.1").setScale(0, BigDecimal.ROUND_HALF_UP)); 
   System.out.println("四舍五入取整:(-2.5)=" + new BigDecimal("-2.5").setScale(0, BigDecimal.ROUND_HALF_UP)); 
   System.out.println("四舍五入取整:(-2.9)=" + new BigDecimal("-2.9").setScale(0, BigDecimal.ROUND_HALF_UP));

   System.out.println("凑整:Math.ceil(-2)=" + (int)Math.ceil(-i)); 
   System.out.println("凑整:Math.ceil(-2.1)=" + (int)Math.ceil(-j)); 
   System.out.println("凑整:Math.ceil(-2.5)=" + (int)Math.ceil(-k)); 
   System.out.println("凑整:Math.ceil(-2.9)=" + (int)Math.ceil(-m)); 
 */}
}
