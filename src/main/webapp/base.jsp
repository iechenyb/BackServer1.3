<%@ page language="java" contentType="text/html; charset=utf-8"    pageEncoding="utf-8"%>
<%
	String basePath = "http://" + request.getServerName() + ":"
			+ request.getServerPort() + request.getContextPath() + "/";
	Object object = request.getSession().getAttribute("username");
	String username = "";
	if (object != null) {
		username = object.toString();
	}
%>          
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>基础学习</title>
</head>
<body> 
<font size=10 color='red'>
    <div>
		<%-- <div class=""><a href="<%=basePath%>upload/doUpload.jsp" target="_blank">废弃上传逻辑</a></div>	 --%>	
		<div class="am-u-sm-2"><a href="<%=basePath%>upload/upload.jsp" target="_blank">单文件上传</a></div>
		<div class="am-u-sm-2"><a href="<%=basePath%>upload/MultiFileUpload.jsp" target="_blank">多文件上传</a></div>		
		<div class="am-u-sm-2"><a href="<%=basePath%>upload/uploadProgess.jsp" target="_blank">进度条监控多文件上传</a></div>
		<div class=""><a href="<%=basePath%>upload/download.jsp" target="_blank">文件下载</a></div>
		<div class=""><a href="<%=basePath%>push/index.jsp" target="_blank">推送查看</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/infor.cyb" target="_blank">information</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/close.cyb" target="_blank">closequtoes(15:30)</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/startup.cyb" target="_blank">startup two</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/shutdown.cyb" target="_blank">shutdown</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/productor.cyb" target="_blank">productor</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/consumer.cyb" target="_blank">consumer</a> </div>
		<div class=""><a href="http://127.0.0.1:8161/admin/queues.jsp" target="_blank">activemq</a> </div>
		<div class=""><a href="<%=basePath%>qutoes/initStock.cyb" target="_blank">initStock</a> </div>
		
	</div>
	</font>
 </body>
 </html> 