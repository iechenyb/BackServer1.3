<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
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
<title>单文件上传</title>
</head>
<body>
<center>
<font size=10 color='green'>
<b>文件上传示例</b>
<form action="<%=basePath%>Upload" method="post" enctype="multipart/form-data">
<%-- application/x-www-form-urlencoded"类型enctype用multipart/form-data，这样可以把文件中的数据作为流式数据上传，不管是什么文件类型，均可上传。--%>
请选择要上传的文件&nbsp;&nbsp;&nbsp;<input type="file" name="upfile" size="50"><br>
<input type="submit" value="提交"></font>
</center>
</body>
</html>