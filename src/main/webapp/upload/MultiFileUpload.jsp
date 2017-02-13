<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>多文件上传</title>
</head>
<body>
<form name="upform" action="<%=basePath%>MutilUpload" method="POST" enctype="multipart/form-data">
<input type ="file" name="file" id="file1"/><br/>
<input type ="file" name="file" id="file2"/><br/>
<input type ="file" name="file" id="file3"/><br/>
<input type="submit" value="Submit" /><br/>
<input type="reset" />
</form>
</body>
</html>