package com.cyb.servlet;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;
public class MutilUpload extends HttpServlet  implements Servlet{
	private static final long serialVersionUID = 1L;
	File tmpDir = null;// ��ʼ���ϴ��ļ�����ʱ���Ŀ¼
    File saveDir = null;// ��ʼ���ϴ��ļ���ı���Ŀ¼
    public MutilUpload() {
        super();
    }
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	}
	protected void doPost(HttpServletRequest request, HttpServletResponse response) 
			throws ServletException, IOException {
		  try {
			  response.setCharacterEncoding("utf-8");
	            if (ServletFileUpload.isMultipartContent(request)) {
	                DiskFileItemFactory dff = new DiskFileItemFactory();// �����ö���
	                dff.setRepository(tmpDir);// ָ���ϴ��ļ�����ʱĿ¼
	                dff.setSizeThreshold(1024000);// ָ�����ڴ��л������ݴ�С,��λΪbyte
	                ServletFileUpload sfu = new ServletFileUpload(dff);// �����ö���
	                sfu.setFileSizeMax(5000000);// ָ�������ϴ��ļ������ߴ�
	                sfu.setSizeMax(10000000);// ָ��һ���ϴ�����ļ����ܳߴ�
	                FileItemIterator fii = sfu.getItemIterator(request);// ����request
	                int i=1;                                                    // ����,������FileItemIterator����
	                while (fii.hasNext()) {
	                    FileItemStream fis = fii.next();// �Ӽ����л��һ���ļ���
	                    String fileName = "";
	                    if (!fis.isFormField() && fis.getName().length() > 0) {// ���˵����з��ļ���
	                    	//infor_bg - 1.png
	                    	fileName = fis.getName();//.substring(fis.getName().lastIndexOf("//"));// ����ϴ��ļ����ļ���
	                        BufferedInputStream in = new BufferedInputStream(fis.openStream());// ����ļ�������
	                        BufferedOutputStream out = new BufferedOutputStream(
	                                new FileOutputStream(new File(saveDir+File.separator+fileName)));// ����ļ������
	                        Streams.copy(in, out, true);// ��ʼ���ļ�д����ָ�����ϴ��ļ���
	                        in.close();
	                        out.close();
	                    }
	                    response.getWriter().println("The "+i+++"st File save path on server is "+saveDir+File.separator+fileName);
	                }
	                response.getWriter().println("All Files upload successfully!!!");// ���ڳɹ���,����������ϴ��ļ��п���,��Ҫ�Ķ�������������
	           
	            }
	        } catch (Exception e) {
	            e.printStackTrace();
	        }
		  
	}
	  public void init() throws ServletException {
	        /* ���ϴ��ļ��к���ʱ�ļ��н��г�ʼ�� */
	        super.init();
	        String tmpPath = "d://tmpdir";
	        String savePath = "d://updir";
	        tmpDir = new File(tmpPath);
	        saveDir = new File(savePath);
	        if (!tmpDir.isDirectory())
	            tmpDir.mkdir();
	        if (!saveDir.isDirectory())
	            saveDir.mkdir();
	    }

}
