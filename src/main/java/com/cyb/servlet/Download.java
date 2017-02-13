package com.cyb.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.util.Streams;

import com.cyb.utils.Contants;

public class Download extends HttpServlet {
	private static final long serialVersionUID = 1L;
    public Download() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doPost(request,response);
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setCharacterEncoding("utf-8");
		String name = Contants.WEBPATH+"upload"+File.separator+request.getParameter("name");
		System.out.println("download path is "+name);
		response.addHeader("Content-Disposition", "attachment; filename="+request.getParameter("name"));  //inline
		response.setContentType("application/octet-stream");//必须指定，否则内容会展示在页面上。
		String type = request.getParameter("type");
		File file = new File(name);
		if(!file.exists()){
			file.createNewFile();
		}
		if("1".equals(type)){
			download1(request,response);
		}else if("2".equals(type)){
			download2(request,response);
		}else{
			download1(request,response);
		}
	}
	
	protected void download1(HttpServletRequest request, HttpServletResponse response) {
		try {
			String name = Contants.WEBPATH+"upload"+File.separator+request.getParameter("name");
			
			OutputStream outputStream =  response.getOutputStream();
			InputStream inputStream = new FileInputStream(new File(name));
			BufferedInputStream in = new BufferedInputStream(inputStream);
			Streams.copy(in, outputStream, true);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	protected void download2(HttpServletRequest request, HttpServletResponse response) {
		try {
			String name = Contants.WEBPATH+"upload"+File.separator+request.getParameter("name");
			OutputStream outputStream =  response.getOutputStream();
			InputStream inputStream = new FileInputStream(new File(name));
			 int x;
			while ((x = inputStream.read()) != -1) {
			  outputStream.write(x);
			}
			inputStream.close();
			outputStream.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	public HttpServletResponse download3(String path, HttpServletResponse response) {
        try {
            // path是指欲下载的文件的路径。
            File file = new File(path);
            // 取得文件名。
            String filename = file.getName();
            // 取得文件的后缀名。
            String ext = filename.substring(filename.lastIndexOf(".") + 1).toUpperCase();

            // 以流的形式下载文件。
            InputStream fis = new BufferedInputStream(new FileInputStream(path));
            byte[] buffer = new byte[fis.available()];
            fis.read(buffer);
            fis.close();
            // 清空response
            response.reset();
            // 设置response的Header
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(filename.getBytes()));
            response.addHeader("Content-Length", "" + file.length());
            OutputStream toClient = new BufferedOutputStream(response.getOutputStream());
            response.setContentType("application/octet-stream");
            toClient.write(buffer);
            toClient.flush();
            toClient.close();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return response;
    }

    public void downloadLocal4(HttpServletResponse response) throws FileNotFoundException {
        // 下载本地文件
        String fileName = "Operator.doc".toString(); // 文件的默认保存名
        // 读到流中
        InputStream inStream = new FileInputStream("c:/Operator.doc");// 文件的存放路径
        // 设置输出的格式
        response.reset();
        response.setContentType("bin");
        response.addHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        // 循环取出流中的数据
        byte[] b = new byte[100];
        int len;
        try {
            while ((len = inStream.read(b)) > 0)
                response.getOutputStream().write(b, 0, len);
            inStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void downloadNet5(HttpServletResponse response) throws MalformedURLException {
        // 下载网络文件
        int bytesum = 0;
        int byteread = 0;

        URL url = new URL("windine.blogdriver.com/logo.gif");

        try {
            URLConnection conn = url.openConnection();
            InputStream inStream = conn.getInputStream();
            FileOutputStream fs = new FileOutputStream("c:/abc.gif");

            byte[] buffer = new byte[1204];
            int length;
            while ((byteread = inStream.read(buffer)) != -1) {
                bytesum += byteread;
                System.out.println(bytesum);
                fs.write(buffer, 0, byteread);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
