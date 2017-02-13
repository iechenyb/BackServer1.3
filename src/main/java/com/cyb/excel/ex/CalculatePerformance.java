package com.cyb.excel.ex;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;

import com.cyb.excel.ExcelUtil;
import com.cyb.excel.ex.bean.JX;
import com.cyb.excel.ex.bean.JX04;
import com.cyb.excel.ex.bean.JX05;
import com.cyb.excel.ex.bean.JX12;
import com.cyb.utils.DataUtils;


public class CalculatePerformance {
    public static Map<String,JX12> map12 = new LinkedHashMap<String,JX12>();
    public static Map<String,JX05> map05 = new LinkedHashMap<String,JX05>();
    public static Map<String,JX05> map05_merge = new LinkedHashMap<String,JX05>();
    public static Map<String,JX05> map05_merge_jl = new LinkedHashMap<String,JX05>();
    public static Map<String,JX04> map04 = new LinkedHashMap<String,JX04>();
    public static List<JX> jxs = new ArrayList<JX>();
     /*excel column formate:column_#_width, excel中每一列的名称*/
    public static final String[] RECORES_COLUMNS = new String[]{
            "部门名称_#_6000",
            "客户经理编号_#_3900",
            "客户经理姓名_#_3900",
            "身份证号_#_3000",
            "当月绩效_#_3000"         
            };
    /*the column will display on xls files. must the same as the entity fields.对应上面的字段.*/
    public static final String[] RECORES_FIELDS = new String[]{
        "bmmc","khjlbh","khjlxm","idcard","dyjx"
    };
    public static void init12Report(){
    	//1 读取12月份的数据
   	   try {      
            String fileName12 = System.getProperty("user.dir")+"\\jx\\"+ConfigUtils.get("lastMonthOfPreYear")+".xls";
            //对读取Excel表格标题测试      
            InputStream is = new FileInputStream(fileName12);      
            String[] title = ReadExcelUtils.readExcelTitle(is);
           /* System.out.println("获得Excel表格的标题:");      
            for (String s : title) {      
                System.out.print(s + " ");      
            }      
            System.out.println();  */   
            //对读取Excel表格内容测试      
            InputStream is2 = new FileInputStream(fileName12);      
            Map<Integer,String> map = ReadExcelUtils.readExcelContent(is2);      
            for (int i=1; i<=map.size(); i++) {      
           	 if(map.get(i).split("#")[3]!=null&&!"".equals(map.get(i).split("#")[3])){
                     JX12 jx12 = new JX12(i+"#"+map.get(i)); 
                     String key = jx12.getKhjlbh().trim()+"#"+jx12.getCz().trim();  
                     map12.put(key, jx12);
                     // System.out.println(map.get(i));
                    // System.out.println(jx12.getXh()+","+jx12.getBmmc()+","+jx12.getKhjlxm());
                }
            }      
        } catch (Exception e) {      
            System.out.println("未找到指定路径的文件!");      
            e.printStackTrace();      
        }   
    }
    public static void init05Report(){
    	//1 读取12月份的数据
    	   try {      
             String fileName05 = System.getProperty("user.dir")+"\\jx\\"+ConfigUtils.get("curMonth")+".xls";
             //对读取Excel表格标题测试      
             InputStream is = new FileInputStream(fileName05);      
             String[] title = ReadExcelUtils.readExcelTitle(is);
            /* System.out.println("获得Excel表格的标题:");      
             for (String s : title) {      
                 System.out.print(s + " ");      
             }      
             System.out.println();     */
             //对读取Excel表格内容测试      
             InputStream is2 = new FileInputStream(fileName05);      
             Map<Integer,String> map = ReadExcelUtils.readExcelContent(is2);      
             for (int i=1; i<=map.size(); i++) {  
            	// System.out.println(i+"#"+map.get(i));
            	 JX05 jx05 = new JX05(i+"#"+map.get(i));
            	 //map.get(i).split("#")[3]!=null&&!"".equals(map.get(i).split("#")[3])
            	 if(!"".equals(jx05.getBmmc())){
                     String key = jx05.getKhjlbh().trim()+"#"+jx05.getCz().trim();  
                     if(!map05.containsKey(key)){
                    	 map05.put(key, jx05);
                     }else{
                    	 double lastSdye =  map05.get(key).getSdye();
                    	 map05.get(key).setSdye(jx05.getSdye()+lastSdye);
                     }
                     //System.out.println(i+"#"+map.get(i));
                 }
             }      
         } catch (Exception e) {      
             System.out.println("未找到指定路径的文件!");      
             e.printStackTrace();      
         }  
    }
    public static void init04Report(){
    	//1 读取12月份的数据
    	   try {      
             String fileName04 = System.getProperty("user.dir")+"\\jx\\"+ConfigUtils.get("lastMonth")+".xls";
             //对读取Excel表格标题测试      
             InputStream is = new FileInputStream(fileName04);      
             String[] title = ReadExcelUtils.readExcelTitle(is);
            /* System.out.println("获得Excel表格的标题:");      
             for (String s : title) {      
                 System.out.print(s + " ");      
             }      
             System.out.println();    */ 
             //对读取Excel表格内容测试      
             InputStream is2 = new FileInputStream(fileName04);      
             Map<Integer,String> map = ReadExcelUtils.readExcelContent(is2);      
             for (int i=1; i<=map.size(); i++) {      
            	 if(map.get(i).split("#")[3]!=null&&!"".equals(map.get(i).split("#")[3])){
            		 JX04 jx04 = new JX04(i+"#"+map.get(i)); 
                     String key = jx04.getKhjlbh().trim()+"#"+jx04.getCz().trim();  
                     if(!"".equals(jx04.getBmmc())){
	                     if(!map04.containsKey(key)){
	                    	 map04.put(key, jx04);
	                     }else{
	                    	 double lastSdye =  map04.get(key).getSdye();
	                    	 map04.get(key).setSdye(jx04.getSdye()+lastSdye);
	                     }
                     }
                     //System.out.println(i+"#"+map.get(i));
                     // System.out.println(jx12.getXh()+","+jx12.getBmmc()+","+jx12.getKhjlxm());
                 }
             }      
         } catch (Exception e) {      
             System.out.println("未找到指定路径的文件!");      
             e.printStackTrace();      
         }  
    }
    public static void emger05(){
    	try {
			List<String> iter = new ArrayList<String>();
			iter.addAll(map05.keySet());
			//String[] iter =(String[]) map05.keySet().toArray();
			 for(int i=0;i<iter.size();i++){
				 String key = iter.get(i);
				 if(map05_merge.containsKey(key)){
					double sum =  map05_merge.get(key).getSdye()+map05.get(key).getSdye();
					map05_merge.get(key).setSdye(sum);
				 }else{
					 map05_merge.put(key, map05.get(key));
					 System.out.println("当月汇总："+key+",汇总时点余额："+map05.get(key).getSdye());
				}
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void calculateEmger05(){
    	try {
			List<String> iter = new ArrayList<String>();
			iter.addAll(map05_merge.keySet());
			Map<String,String> errCz = new HashMap<String,String>();
			 for(int i=0;i<iter.size();i++){
				 String key = iter.get(i);
				 String cz = key.split("#")[1];
				 Double curJx = 0.0;
				 if("活期".equals(cz)){//（5月活期月日均余额-12月活期月日均余额）*6
					 if(map12.get(key)==null){
						 curJx = (map05_merge.get(key).getRjye()-0)*6;
					 }else{
						 curJx = (map05_merge.get(key).getRjye()-map12.get(key).getRjye())*6;
					 }
				 }else if ("定活两便".equals(cz)||"通知".equals(cz)){//（5月活期时点余额-12月活期时点余额）*6
					 if(map12.get(key)==null){
						 curJx = (map05_merge.get(key).getSdye()-0)*6;
					 }else{
						 curJx = (map05_merge.get(key).getSdye()-map12.get(key).getSdye())*6;
					 }
				 }else if("三个月定期".equals(cz)||"三个月协议定期".equals(cz)){//（5月时点余额-4月时点余额）*5；
					 if(map04.get(key)==null){
						 curJx = (map05_merge.get(key).getSdye()-0)*5;
					 }else{
					    curJx = (map05_merge.get(key).getSdye()-map04.get(key).getSdye())*5;
					 }
					 if(curJx<0){
						 curJx =0.0;
					 }
				 }else if("六个月定期".equals(cz)||"六个月协议定期".equals(cz)){//（5月时点余额-4月时点余额）*9
					 if(map04.get(key)==null){
						 curJx = (map05_merge.get(key).getSdye()-0)*9;
					 }else{
					     curJx = (map05_merge.get(key).getSdye()-map04.get(key).getSdye())*9;
					 }
					 if(curJx<0){
						 curJx =0.0;
					 }
				 }else if("一年定期".equals(cz)||"一年协议定期".equals(cz)||"二年定期".equals(cz)
						 ||"二年协议定期".equals(cz)||"三年".equals(cz)
						 ||"五年".equals(cz)||"五年定期".equals(cz)||
						 "两年协议定期".equals(cz)||"两年定期".equals(cz)||
						 "三年定期".equals(cz)||"三年协议定期".equals(cz)
						 ){//（5月时点余额-4月时点余额）*16
					 if(map04.get(key)==null){
						 curJx = (map05_merge.get(key).getSdye()-0)*16;
					 }else{
					     curJx = (map05_merge.get(key).getSdye()-map04.get(key).getSdye())*16;
					 }
				 }else{
					 errCz.put(cz, cz);
				 }
				 map05_merge.get(key).setCurjx(curJx);
			 }//end for
			 if(errCz.size()>0){
				 new Exception("错误的储蓄种类："+errCz.keySet()).printStackTrace();
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void getEmger05JX(){
    	try {
			List<String> iter = new ArrayList<String>();
			iter.addAll(map05_merge.keySet());
			 for(int i=0;i<iter.size();i++){
				 String[] key = iter.get(i).split("#");
				 if(map05_merge_jl.containsKey(key[0])){
					 double jx1 = map05_merge_jl.get(key[0].replace(" ", "")).getCurjx();
					 double jx2 = map05_merge.get(iter.get(i)).getCurjx();
					 double total = jx1+jx2;
					 map05_merge_jl.get(key[0].replace(" ", "")).setCurjx(total);
				 }else{
					 map05_merge_jl.put(key[0].replace(" ", ""), map05_merge.get(iter.get(i)));
				 }
			 }
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void formatRestult(){
    	try {
    		Double max = 0.0;
    		Double min = 0.0;
    		Double avg =0.0;
    		Double sum =0.0;
			List<String> iter = new ArrayList<String>();
			iter.addAll(map05_merge_jl.keySet());
			 for(int i=0;i<iter.size();i++){
				 String key = iter.get(i);
				 JX jx = new JX();
				 jx.setBmmc(map05_merge_jl.get(key).getBmmc());
				 String dyjx = DataUtils.roundStr(map05_merge_jl.get(key).getCurjx(),4);
				 if(Double.valueOf(dyjx)>=max){
					 max =Double.valueOf(dyjx); 
				 }
				 if(Double.valueOf(dyjx)<=min){
					 min =Double.valueOf(dyjx); 
				 }
				 sum = sum +Double.valueOf(dyjx);
				 jx.setDyjx(Double.valueOf(dyjx));
				 jx.setIdcard("");
				 jx.setKhjlbh(map05_merge_jl.get(key).getKhjlbh());
				 jx.setKhjlxm(map05_merge_jl.get(key).getKhjlxm());
				 jxs.add(jx);
			 }
			 System.out.println("统计人员总数="+jxs.size()+",最大值="+max+",最小值="+min+",平均值="+(sum/jxs.size()));
		} catch (Exception e) {
			e.printStackTrace();
		}
    }
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, IOException {
    	 try {
			ConfigUtils.init();
			init12Report();
			init04Report();
	    	init05Report();
	    	emger05();
	    	calculateEmger05();
	    	getEmger05JX();
	    	formatRestult();
		} catch (Exception e) {
			e.printStackTrace();
		}
        HSSFWorkbook workbook = new HSSFWorkbook();
        ExcelUtil<JX> userSheet = new ExcelUtil<JX>();
        userSheet.creatAuditSheet(workbook, "user sheet xls",jxs, RECORES_COLUMNS, RECORES_FIELDS);
        FileOutputStream fileOut = new FileOutputStream(System.getProperty("user.dir")+File.separator+ConfigUtils.get("outFileName")+".xls"); 
        System.out.println("输出文件路径："+System.getProperty("user.dir")+File.separator+ConfigUtils.get("outFileName")+".xls");
        workbook.write(fileOut); 
        fileOut.close(); 
    }

}
