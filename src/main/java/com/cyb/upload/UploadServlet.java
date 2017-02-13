package com.cyb.upload;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

public class UploadServlet extends HttpServlet {

    private File uploadTemp=null;  
    private File uploadPath=null; 
    
	/**
	 * Constructor of the object.
	 */
	public UploadServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

	/**
	 * The doGet method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to get.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		 //���û���,index.jsp��̨��ʹ��XmlHttpRequest���ñ�Servlet��doGet��������session�л�ȡ���µ��ϴ��������  
        response.setHeader("Cache-Control", "no-store");  
        response.setHeader("Pragrma", "no-cache");  
        response.setDateHeader("Expires", 0);  
        response.setContentType("text/html;charset=utf-8");  
        UploadStatus status = (UploadStatus) request.getSession(true)  
                .getAttribute("uploadStatus");  
      
        if (status == null) {  
            response.getWriter().println("上传状态为空！");  
              
            return;  
        }  
        long startTime = status.getStartTime();  
        long currentTime = System.currentTimeMillis();  
  
        // �Ѵ����ʱ�� ��λ��s  
        long time = (currentTime - startTime) / 1000 + 1;  
  
        // �����ٶ� ��λ��byte/s  
        double velocity = ((double) status.getBytesRead()) / (double) time;  
  
        // ������ʱ�� ��λ��s  
        double totalTime = status.getContentLength() / velocity;  
  
        // ����ʣ��ʱ�� ��λ��s  
        double timeLeft = totalTime - time;  
  
        // ����ɵİٷֱ�  
        int percent = (int) (100 * (double) status.getBytesRead() / (double) status  
                .getContentLength());  
  
        // ������� ��λ��M  
        double length = ((double) status.getBytesRead()) / 1024 / 1024;  
  
        // �ܳ��� ��λ��M  
        double totalLength = ((double) status.getContentLength()) / 1024 / 1024;  
  
        // ��ʽ���ٷֱ�||�������(M)||�ļ��ܳ���(M)||��������(K)||����ʱ��(s)||������ʱ��(s)||����ʣ��ʱ��(s)||�����ϴ��ڼ����ļ�  
        String value = percent + "||" + length + "||" + totalLength + "||"  
                + velocity + "||" + time + "||" + totalTime + "||" + timeLeft  
                + "||" + status.getItems();  
  
        response.getWriter().println(value);  
	}
	
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		File file=null;  
        String description=null;  
          
        //������Ӧ��ʽ�������������ʽ����Ϊ�ļ��Ƕ����Ƶģ�����ʹ��UTF-8��ʽ���������ݣ�  
        response.setContentType("text/html;charset=utf-8");  
          
        PrintWriter out=response.getWriter();  
        out.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.01 Transitional//EN\">");  
        out.println("<HTML>");  
        out.println("<HEAD><TITLE>helloworld</TITLE></HEAD>");  
        out.println("<BODY style='margin:50px'>");  
        out.println("hahaha<BR/>");  
          
        UploadStatus status=new UploadStatus();  
        UploadListener listener=new UploadListener(status);  
        /* 
         * �� UploadStatus �ŵ� session ��,���� 
		 * ���������������ĵ�ǰHttpSession�����û�е�ǰ�Ự�ʹ�������ʵ�ģ�����һ���µĻỰ�� 
		 * ��������Ǽٵģ���Ҫ����û����Ч��HttpSession�������������null�� 
         */  
        request.getSession(true).setAttribute("uploadStatus", status);  
          
        //�������ڴ��̵Ĺ�������Դ��ļ�����ʱ�ļ����洢�ڴ���  
        DiskFileItemFactory factory=new DiskFileItemFactory();  
        //���û�������С,�������ļ�ֱ��д�뵽���̵Ĵ�С�����ż���  
        factory.setSizeThreshold(10240);  //����Ĭ��10KB  
        //�������ڴ������õĴ�С��ֵ���õ���ʱ�洢�ļ�Ŀ¼��  
        factory.setRepository(uploadTemp);  
        //����һ���ļ��ϴ��ľ��  
        ServletFileUpload upload=new ServletFileUpload(factory);  
        //max upload length limit unit is MB          
        upload.setSizeMax(1377329153);  
        upload.setHeaderEncoding("utf-8");  
          
        // ���� listener  
        upload.setProgressListener(listener);  
          
        try {  
            //�������������LIST��  
            List<FileItem> list =upload.parseRequest(request);  
            out.println(" FileItem ... <br/>");  
            // ���� list �����е� FileItem  
            for(FileItem item:list)  
            {  
                // ����� �ı���  
                if(item.isFormField())  
                {  
                    if(item.getFieldName().equals("description1")||item.getFieldName().equals("description2"))  
                    {  
                                              
                        description = item.getString("UTF-8");      
                        System.out.println("item file"+item.getFieldName()+" -----------"+description);      
                    }  
                }  
                else   
                {  
                    //����Ϊ�ļ���,��getNameΪNull˵��û��ѡ���ļ�  
                    if((item.getFieldName().equals("file1")||item.getFieldName().equals("file2"))  
                            &&item.getName()!=null&&!item.getName().equals(""))  
                    {  
                        try   
                        {  
                            // ͳһ Linux �� windows ��·���ָ���  
                            String fileName = item.getName();  
                            //fileName = fileName.substring(fileName.lastIndexOf("\\"));  
  
                            // ���������ļ������� upload �ļ�����  
                            file=new File(uploadPath,fileName);  
                            if(!file.getParentFile().exists())  
                                file.getParentFile().mkdirs();  
                            if(!file.exists())  
                                file.createNewFile();  
                              
                            item.write(file);  
                            out.println("file save path on server is "+file);
                            System.out.println("———————— "+fileName+" ... <br/>"+file+"<BR/>");      
                        } catch (Exception e) {  
                            System.out.println("Request - "+e.getMessage());  
                        }  
                        finally //��������ɾ��������ֶ����ݵ���ʱ�ļ�  
                        {  
                            item.delete();  
                        }  
                    }  
                }  
            }  
            System.out.println("File upload sucess!");  
        } catch (Exception e) {  
            System.out.println("Request handled failed ,"+e.getMessage());  
        }  
        out.flush();  
        out.close();  
	}

	/**
	 * Initialization of the servlet. <br>
	 *
	 * @throws ServletException if an error occurs
	 */
	public void init() throws ServletException {
		// Put your code here
		uploadPath=new File(this.getServletContext().getRealPath("upload"));  
        if(!uploadPath.exists())  
            uploadPath.mkdirs();          
        uploadTemp=new File(this.getServletContext().getRealPath("upload/temp"));  
        if(!uploadTemp.exists())  
            uploadTemp.mkdirs();  
	}

}
