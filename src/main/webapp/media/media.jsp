 <%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
   <%      
   String path = request.getContextPath();    
   String basePath = request.getScheme()+"://"+request.getServerName()+":"+request.getServerPort()+path+"/"; %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
<object width="452" height="339" classid="clsid:D27CDB6E-AE6D-11cf-96B8-444553540000">   
  <param name="movie"  value="<%=basePath%>tools/player.swf?fileName=<%=basePath%><%=90%>" />     
  <embed  src="<%=basePath%>tools/player.swf?fileName=<%=basePath%><%=90%>"    width="98%" height="90%"></embed>   
</object> 
</body>
</html>