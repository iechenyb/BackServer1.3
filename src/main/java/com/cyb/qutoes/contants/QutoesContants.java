package com.cyb.qutoes.contants;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.web.socket.WebSocketSession;

import com.cyb.qutoes.bean.RealQutoes;

public class QutoesContants {
  public static boolean SWTICH = true;//能否抓取行情
  public static String minqutoesMessage = "minqutoes" ;
  public static String myConcernMessage = "myConcern" ;
  public static String khistoryMessage = "khistory" ;
  public static String textMessage = "text" ;
  public static String reqMessage_Minqutoes = "MINQUTOES";
  public static String reqMessage_KHISTORY = "KHISTORY";
  public static final BlockingQueue<WebSocketSession> sessions = new ArrayBlockingQueue<WebSocketSession>(20000);
  public static final ExecutorService pushPool = Executors.newFixedThreadPool(1);
  public static final ExecutorService pushNewPricePool = Executors.newFixedThreadPool(1);
  public static final Map<String ,List<String>> KLineOfTODAY= new HashMap<String, List<String>>();
  public static final Map<String,String> holidays = new HashMap<String, String>();
  public static final Map<String,String> lastPriceMap = new HashMap<String, String>();
  public static final Map<String,String> needPushNewPriceCodesMap = new HashMap<String, String>();
  public static  String LASTESTDAY = "";//最新交易日 （每日开盘时更新为当日，次日0：0-9：30分之间依旧是上一个交易日的值）
  
  public static Set<String> tables= null;
  static {
	  tables = new HashSet<String>();
	  tables.add("GRAILINDICATORCLOSE");
	  tables.add("REALTIMEQUTOES");
	  tables.add("CLOSEQUTOES");
	  tables.add("MINUTEQUTOESCURRDAY");
	  tables.add("GRAILQUTOES");
	  tables.add("MINUTEQUTOES");
	  tables.add("STOCK");
	  tables.add("STOCKCONFIG");
	  tables.add("HOLIDAYCONFIG");
	  tables.add("IDEA");
	  tables.add("USR");
  }
  public static Map<String,String> tableMaps = null;
  public static String TABLEGRAILINDICATORCLOSE = "create table grailindicatorclose(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),high_ double,open_ double,low_ double,close_ double,oper_time timestamp)";
  public static String TABLEREALTIMEQUTOES = "create table realtimequtoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp)";
  public static String TABLECLOSEQUTOES = "create table closequtoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp)";
  public static String TABLEMINUTEQUTOESCURRDAY = "create table minutequtoescurrday(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp)";
  public static String TABLEGRAILQUTOES = "create table grailqutoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,high_ double,low_ double,oper_time timestamp)";
  public static String TABLEMINUTEQUTOES = "create table minutequtoes(id_ varchar(50),record_date_ long,name_ varchar(50),code_ VARCHAR(20),open_ double,close_ double,price_ double,preclose_ double,high_ double,low_ double,day_ varchar(14),time_ varchar(20),oper_time timestamp)";
  public static String TABLESTOCK = "CREATE TABLE stock(id_ varchar(50) PRIMARY KEY,   code_ VARCHAR(20),exchange_ varchar(20),name_ varchar(100),oper_time timestamp)";
  public static String TABLESTOCKCONFIG = " create table  stockconfig(alias varchar(100),curjyr long)";
  public static String TABLEHOLIDAYCONFIG =" create table holidayconfig(jjr long)";
  public static String TABLEIDEA = "create table idea(id_ varchar(50),phone varchar(11),email varchar(50),userType long ,ideaType long,fileId varchar(50),message varchar(2000))";
  public static String TABLEUSER = "create table usr(id_ varchar(50),username varchar(50),password varchar(50),roleId long ,loginstatus long,registerTime long,status long,email varchar(50),phone long)";
  public static String TABLEUSERCONCERN = "create table userconcern(id_ varchar2(50),code_ varchar2(20),userid varchar2(50),create_time timestamp);";
  static{
	  tableMaps = new HashMap<String, String>();
	  tableMaps.put("GRAILINDICATORCLOSE",TABLEGRAILINDICATORCLOSE );
	  tableMaps.put("REALTIMEQUTOES", TABLEREALTIMEQUTOES);
	  tableMaps.put("CLOSEQUTOES", TABLECLOSEQUTOES);
	  tableMaps.put("MINUTEQUTOESCURRDAY", TABLEMINUTEQUTOESCURRDAY);
	  tableMaps.put("GRAILQUTOES", TABLEGRAILQUTOES);
	  tableMaps.put("MINUTEQUTOES", TABLEMINUTEQUTOES);
	  tableMaps.put("STOCK", TABLESTOCK);
	  tableMaps.put("STOCKCONFIG", TABLESTOCKCONFIG);
	  tableMaps.put("HOLIDAYCONFIG", TABLEHOLIDAYCONFIG);
	  tableMaps.put("IDEA", TABLEIDEA);
	  tableMaps.put("USR", TABLEUSER);
	  tableMaps.put("USERCONCERN", TABLEUSERCONCERN);
  }
  public static List<Map<String,String>> INDUSTRYSORT = null;
  //<sh600868,A>
  public static Map<String,String> CODEINDUSTRY = null;
  public static Map<String,RealQutoes> realQutoesMap = null;
}
