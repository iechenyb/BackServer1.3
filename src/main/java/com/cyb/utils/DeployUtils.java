package com.cyb.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DeployUtils {
	//复制文件夹下所有文件
	public static void copy(File[] fl, File file) {
	    if (!file.exists()) // 如果文件夹不存在
	        file.mkdir(); // 建立新的文件夹
	    for (int i = 0; i < fl.length; i++) {
	        if (fl[i].isFile()) { // 如果是文件类型就复制文件
	        	/*if(fl[i].getName().equals("App.properties")){
	        		System.out.println(fl[i].getName());
	        	}*/
	            try {
	                FileInputStream fis = new FileInputStream(fl[i]);
	                FileOutputStream out = new FileOutputStream(new File(file
	                        .getPath()
	                        + File.separator + fl[i].getName()));
	                int count = fis.available();
	                byte[] data = new byte[count];
	                if ((fis.read(data)) != -1) {
	                    out.write(data); // 复制文件内容
	                }
	                out.close(); // 关闭输出流
	                fis.close(); // 关闭输入流
	            } catch (Exception e) {
	                e.printStackTrace();
	            }
	        }
	        if (fl[i].isDirectory()) { // 如果是文件夹类型
	            File des = new File(file.getPath() + File.separator
	                    + fl[i].getName());
	            des.mkdir(); // 在目标文件夹中创建相同的文件夹
	            copy(fl[i].listFiles(), des); // 递归调用方法本身
	        }
	    }
	}    
	
	public static void copyEstock(){
		File sourFile = null, desFile = null;
	    String sourFolder = "D:\\apache-tomcat-7.0.52\\EStock"; // 可以修改源文件夹路径
	    String desFolder = "D:\\chenyb\\myproject\\deploy\\EStock"; // 可以修改目标文件夹路径
	    sourFile = new File(sourFolder);
	    if (!sourFile.isDirectory() || !sourFile.exists()) {
	        System.out.println("源文件夹不存在");
	    }else{ desFile = new File(desFolder);
		    desFile.mkdir();
		    copy(sourFile.listFiles(), desFile); //调用copy()方法
		    System.out.println("文件夹复制成功！");
	    }
	}
	public static void copyBackServer(){
		File sourFile = null, desFile = null;
	    String sourFolder = "D:\\apache-tomcat-7.0.52\\BackServer"; // 可以修改源文件夹路径
	    String desFolder = "D:\\chenyb\\myproject\\deploy\\BackServer"; // 可以修改目标文件夹路径
	    sourFile = new File(sourFolder);
	    if (!sourFile.isDirectory() || !sourFile.exists()) {
	        System.out.println("源文件夹不存在");
	    }else{ desFile = new File(desFolder);
		    desFile.mkdir();
		    copy(sourFile.listFiles(), desFile); //调用copy()方法
		    System.out.println("文件夹复制成功！");
	    }
	}
	
	public static void resetApp1(){
		File sourFile = null, desFile = null;
	    String src = "D:\\apache-tomcat-7.0.52\\EStock\\WEB-INF\\classes\\AppServer.properties"; // 可以修改源文件夹路径
	    String dest = "D:\\chenyb\\myproject\\deploy\\EStock\\WEB-INF\\classes\\App.properties"; // 可以修改目标文件夹路径
	    try {
			FileUtils.copyFile(src, dest);
			System.out.println("文件复制成功！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void resetApp2(){
		File sourFile = null, desFile = null;
	    String src = "D:\\apache-tomcat-7.0.52\\BackServer\\WEB-INF\\classes\\AppServer.properties"; // 可以修改源文件夹路径
	    String dest = "D:\\chenyb\\myproject\\deploy\\BackServer\\WEB-INF\\classes\\App.properties"; // 可以修改目标文件夹路径
	    try {
			FileUtils.copyFile(src, dest);
			System.out.println("文件复制成功！");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	   /* if (!sourFile.isDirectory() || !sourFile.exists()) {
	        System.out.println("源文件夹不存在");
	    }else{ desFile = new File(desFolder);
		    desFile.mkdir();
		    copy(sourFile.listFiles(), desFile); //调用copy()方法
		    System.out.println("文件夹复制成功！");
	    }*/
	}
	public static void main(String[] args) {
		copyEstock();
		copyBackServer();
		resetApp1();
		resetApp2();
	}
}
