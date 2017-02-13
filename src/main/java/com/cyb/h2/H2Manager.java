package com.cyb.h2;

import java.sql.SQLException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.h2.tools.Server;


public class H2Manager {
	public static Log log = LogFactory.getLog(H2Manager.class);
	static Server server; 
	 public static void initServer(){
		 try {
			server = Server.createTcpServer();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	 }
	 public static void start(){

		 if(server!=null){
			try {
				server.start();
				log.info("h2 server start!");
			} catch (SQLException e) {
				e.printStackTrace();
			} 
		 }
	 }
	 public static void stop(){
		 if(server!=null){
			try {
				server.stop();
				log.info("h2 server end!");
			} catch (Exception e) {
				e.printStackTrace();
			} finally{
				server = null;
			}
		 }
	 }
	 public static void shutdown(){
		 if(server!=null){
			try {
				server.shutdown();
				log.info("h2 server shutdown!");
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				server = null;
			}
		 }
	 }
}
