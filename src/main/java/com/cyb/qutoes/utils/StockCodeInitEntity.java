package com.cyb.qutoes.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;




import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.springframework.stereotype.Repository;

import com.cyb.vo.Stock;
import com.cyb.qutoes.contants.QutoesContants;
import com.cyb.utils.Contants;
import com.cyb.utils.ExcelUtils;
import com.cyb.utils.FileUtils;
import com.cyb.utils.PropertyUtil;
import com.cyb.utils.UUIDUtils;
@Repository
public class StockCodeInitEntity {
	
 
 public  void initIndustry(){
	 String root = PropertyUtil.getValueByKey("App", "stockFile");
	 String industryFile = PropertyUtil.getValueByKey("App", "industryName");
	 String file = Contants.WEBPATH+root+File.separator+industryFile;
	 StringBuffer content = FileUtils.readFileByLines(file);
	 QutoesContants.INDUSTRYSORT = new ArrayList<Map<String,String>>();
	 String industrys[] = content.toString().split(",");
	 Map<String,String> species = null;
	 for(int i=0;i<industrys.length;i++){
		 species = new HashMap<String, String>();
		 species.put("type",industrys[i].split("#")[0]);
		 species.put("name", industrys[i].split("#")[1]);
		 QutoesContants.INDUSTRYSORT.add(species);
	 }
	 System.out.println("行业分类："+QutoesContants.INDUSTRYSORT);
   }
  public List<Stock> initSHStocks(String fileName,String industry){ 
	  List<Stock> lst = new ArrayList<Stock>();
      POIFSFileSystem fs =null;      
      HSSFWorkbook wb = null;      
      HSSFSheet sheet =null;      
      HSSFRow row = null;    
      InputStream is =null;
      Stock stock = null;  
      File file = new File(fileName);
      if(!file.exists()){
    	 System.out.println("文件"+fileName+"不存在！");  
    	  return null;
      }
      try {      
    	  is = new FileInputStream(fileName);      
          fs = new POIFSFileSystem(is);      
          wb = new HSSFWorkbook(fs);      
      } catch (IOException e) {      
          System.out.println("exception occur:"+fileName+"\n"+e.toString());      
      }      
      sheet = wb.getSheetAt(0);      
      //得到总行数      
      int rowNum = sheet.getLastRowNum();      
      row = sheet.getRow(0);      
      int colNum = row.getPhysicalNumberOfCells();      
      //正文内容应该从第二行开始,第一行为表头的标题      
      for (int i = 1; i <= rowNum; i++) {      
          row = sheet.getRow(i);      
          int j = 0;     
          //公司代码-0 	公司简称-1 	A股代码-2 	A股简称-3 	A股上市日期-4	A股总股本	A股流通股本
          stock = new Stock();
          stock.setIndustry(industry);
          stock.setExchange("sh");
          stock.setId(UUIDUtils.getUUID());
          while (j<colNum) {      
          //每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据      
          //也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean      
        	  String coljValue = ExcelUtils.getStringCellValue(row.getCell((short) j)).trim();
        	  if(j==0){
        		  stock.setCode(coljValue.substring(0, coljValue.indexOf(".")));
        	  }else if(j==1){
        		  stock.setName(coljValue);
        	  }
        	  j++;
          }      
          lst.add(stock);
          System.out.println(stock);
          stock = null;
      }//end for   
      return lst;
  }
  public List<Stock> initSZStocks(String fileName,String industry){ 
	  List<Stock> stocks = new ArrayList<Stock>();
      POIFSFileSystem fs =null;      
      HSSFWorkbook wb = null;      
      HSSFSheet sheet =null;      
      HSSFRow row = null;    
      InputStream is =null;
      Stock stock = null;  
      File file = new File(fileName);
      if(!file.exists()){
    	 System.out.println("文件"+fileName+"不存在！");  
    	  return null;
      }
      try {      
    	  is = new FileInputStream(fileName);      
          fs = new POIFSFileSystem(is);      
          wb = new HSSFWorkbook(fs);      
	      sheet = wb.getSheetAt(0);      
	      //得到总行数      
	      int rowNum = sheet.getLastRowNum();      
	      row = sheet.getRow(0);      
	      int colNum = row.getPhysicalNumberOfCells();      
	      //正文内容应该从第二行开始,第一行为表头的标题      
	      for (int i = 1; i <= rowNum; i++) {      
	          row = sheet.getRow(i);      
	          int j = 0;     
	          //公司代码-0 	公司简称-1 	A股代码-2 	A股简称-3 	A股上市日期-4	A股总股本	A股流通股本
	          stock = new Stock();
	          stock.setId(UUIDUtils.getUUID());
	          stock.setIndustry(industry);
	          stock.setExchange("sz");
	          while (j<colNum) {      
	          //每个单元格的数据内容用"-"分割开，以后需要时用String类的replace()方法还原数据      
	          //也可以将每个单元格的数据设置到一个javabean的属性中，此时需要新建一个javabean      
	        	  String coljValue = ExcelUtils.getStringCellValue(row.getCell((short) j));
	        	  if(coljValue==null||coljValue.equals("")){
	        		  break;
	        	  }
	        	  if(j==0){
	        		  stock.setCode(coljValue);
	        	  }else if(j==1){
	        		  stock.setName(coljValue);
	        	  }
	        	  j++;
	          }      
	          stocks.add(stock);
	          System.out.println("#"+i+" "+stock);
	      }    
      } catch (Exception e) {      
          e.printStackTrace();      
      }
	return stocks; 
  }
  
  
  public static void main(String[] args) {
	  StockCodeInitEntity obj  =new StockCodeInitEntity();
	  String fileName = "d:\\file\\sh-A.xls";
	  obj.initIndustry();
	  /*for(int i=0;i<QutoesContants.INDUSTRYSORT.size();i++){
		  String industry = QutoesContants.INDUSTRYSORT.get(i).get("type");
		  fileName="d:\\file\\sh-"+QutoesContants.INDUSTRYSORT.get(i).get("type")+".xls";
		  obj.initSHStocks(fileName,industry);
	  }*/
	  for(int i=0;i<QutoesContants.INDUSTRYSORT.size();i++){
		  String industry = QutoesContants.INDUSTRYSORT.get(i).get("type");
		  fileName="d:\\file\\SZ-"+QutoesContants.INDUSTRYSORT.get(i).get("type")+".xls";
		  obj.initSZStocks(fileName,industry);
	  }
  }
}
