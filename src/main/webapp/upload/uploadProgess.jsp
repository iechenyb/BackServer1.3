<%@ page language="java" import="java.util.*" pageEncoding="UTF-8"%>
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
 <title>带进度条的文件上传</title> 
 <style type="text/css">
	 #progressBar{width:400px;height:12px;background:#FFFFFF;border:1px solid #000000;padding:1px;}
	 #progressBarItem{width:30%;height:100%;background:#FF0000;}
 </style>
 <script type="text/JavaScript">
 //默认为已经完成上传操作
 var basePath = '<%=basePath%>';
 var _finished=true;
 function $(obj)
 {
 return document.getElementById(obj);
 }
 //<!--显示进度条等信息-->
 function showStatus()
 {
 _finished=false;
 $('status').style.display='block';
 $('progressBarItem').style.width='1%';
 $('btnSubmit').disabled=true;
 //<!--隔1秒后执行一次-->
 setTimeout("requestStatus()",1000);
 }
 //<!--发送请求获取文件上传状态-->
 function requestStatus()
 {
 if(_finished)
 return;
 var req=createRequest();
 req.open("GET",basePath+"UploadServlet");
 req.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
 req.onreadystatechange=function(){callback(req);};
 //我们的实例在 open() 的第三个参数中使用了 "true"。该参数规定请求是否异步处理。
 //True 表示脚本会在 send() 方法之后继续执行，而不等待来自服务器的响应。
 
 req.send(null);
 setTimeout("requestStatus()",1000);
 }
 function createRequest()
 {
  if(window.XMLHttpRequest)//ns
  {
   return new XMLHttpRequest();
  }else//IE
  {
   try{
    return new ActiveXObject("Msxml2.XMLHTTP");
   }catch(e){
    return new ActiveXObject("Microsoft.XMLHTTP");
   }
  }
  return null;
 }
 function callback(req)
 {
   //请求结束后 
 if(req.readyState==4)
 {
  //如果发生错误，则显示错误信息 
 if(req.status!=200)
 {
 _debug("发生错误。 req.status: " + req.status + "");
    return;
 }
 
   var ss = req.responseText.split("||");
   
   // 格式：百分比||已完成数(M)||文件总长度(M)||传输速率(K)||已用时间(s)||估计总时间(s)||估计剩余时间(s)||正在上传第几个文件
   $('progressBarItem').style.width = '' + ss[0] + '%'; 
   $('statusInfo').innerHTML = '已完成百分比: ' + ss[0] + '% <br />已完成数(M): ' + ss[1] + '<br/>文件总长度(M): ' + ss[2] + '<br/>传输速率(K): ' + ss[3] + '<br/>已用时间(s): ' + ss[4] + '<br/>估计总时间(s): ' + ss[5] + '<br/>估计剩余时间(s): ' + ss[6] + '<br/>正在上传第几个文件: ' + ss[7];
  
   if(ss[1] == ss[2])
 {
   _finished = true;
   $('statusInfo').innerHTML += "<br/><br/><br/>上传已完成。";  
   $('btnSubmit').disabled = false;
   }
   _debug("status.jsp 返回值：" + req.responseText);
  
 }
 }
 function _debug(obj)
 {
 //var div=document.createElement("DIV");
 $('debug').innerHTML="[debug]:"+obj+"<br/>"; 
 //document.body.appendChild(div);
 
 }
 </script>
 </head> 
 <body style="margin:50px">
 <iframe name="upload_iframe" width="0" height="0" frameborder="0" ></iframe>
  
  <form action="<%=basePath%>UploadServlet" method="post" enctype="multipart/form-data"
  target="upload_iframe" onsubmit="showStatus();">
  <p>上传文件：</p>
	  文件1：<input type="file" name="file1" /><br/>
	  描述：<input type="text" name="description1" /><br/>
	  文件2：<input type="file" name="file2" /><br/>
	  描述：<input type="text" name="description2" /><br/>
  <input type="submit" id="btnSubmit" value=" 上 传 " /> 
  </form>
  <div id="status" style="display:none;position:relative;line-height:100%;opacity:1;">
  上传进度：
  <div id="progressBar" ><div id="progressBarItem" /></div>
  <div id="statusInfo" style="margin:10px 0px 0px 0px;"/>
  </div>
  <BR/>
  <div id="debug" />
 </body>
 </html>



