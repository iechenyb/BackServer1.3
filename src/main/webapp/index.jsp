<!DOCTYPE html>
<html>
<head>
    <title>WebSocket/SockJS Echo Sample (Adapted from Tomcat's echo sample)</title>
    <style type="text/css">
        #connect-container {
            float: left;
            width: 400px;
            height: 600px;
            border :solid red 0px;
        }

        #connect-container div {
            padding: 5px;
            float: left;
            margin-left: 15px;
            width: 600px;
        }

        #console-container {
            float: left;
            margin-left: 15px;
            width: 600px;
        }

        #console {
            border: 1px solid #CCCCCC;
            border-right-color: #999999;
            border-bottom-color: #999999;
            height: 170px;
            overflow-y: scroll;
            padding: 5px;
            width: 100%;
            height:600px;
        }

        #console p {
            padding: 0;
            margin: 0;
        }
    </style>

    <script src="http://cdn.sockjs.org/sockjs-0.3.min.js"></script>
    <script type="text/javascript" src="json2.js"></script>
    <script type="text/javascript">
        var ws = null;
        var url = null;
        var transports = [];
        var username ='';
        function setConnected(connected) {
            document.getElementById('connect').disabled = connected;
            document.getElementById('disconnect').disabled = !connected;
            document.getElementById('echo').disabled = !connected;
        }

        function connect() {
        	username =  document.getElementById('username').value;
        	if(username == ''){
        		alert("user is needed!!");
        		return ;
        	} 
        	//alert("url:"+url);
            if (!url) {
                alert('Select whether to use W3C WebSocket or SockJS');
                return;
            }

            ws = (url.indexOf('sockjs') != -1) ? 
                new SockJS(url, undefined, {protocols_whitelist: transports}) : new WebSocket(url);

            ws.onopen = function () {
                setConnected(true);
                log('Info: connection opened.');
            };
            ws.onmessage = function (event) {
            	var data=eval("("+event.data+")");
            	if(data.msgType=='text'){
               	 log(data.content);
            	}else if(data.msgType=='users'){
            		log("useronline:"+data.lst);
            		document.getElementById('users').value=data.lst;
            	}
            };
            ws.onclose = function 
            (event) {
                setConnected(false);
                var user = document.getElementById('username').value;
                log('System tips:connection closed!');
                log(user+",you have exit the chat room,you can login agin to chatting!");
            };
        }

        function disconnect() {
            if (ws != null) {
                ws.close();
                ws = null;
            }
            setConnected(false);
        }

        function echo() {
            if (ws != null) {
                var message = document.getElementById('message').value;
                var toUser = document.getElementById('toUser').value;
                log(username+':' + message);
               // var data=JSON.stringify({msg:message,toUser:toUser});
                var data = "{msg:'"+message+"',"+"toUser:'"+toUser+"'}";
                ws.send(data);
            } else {
                alert('connection not established, please connect.');
            }
        }

        function updateUrl(urlPath) {
        	username =  document.getElementById('username').value;
        	if(username == ''){
        		alert("user is needed!!");
        		return ;
        	} 
            if (urlPath.indexOf('sockjs') != -1) {
                url = urlPath;
                document.getElementById('sockJsTransportSelect').style.visibility = 'visible';
            } else {
              if (window.location.protocol == 'http:') {
                  url = 'ws://' + window.location.host + urlPath+"?name="+username;
              } else {
                  url = 'wss://' + window.location.host + urlPath+"?name="+username;
              }
              document.getElementById('sockJsTransportSelect').style.visibility = 'hidden';
            }
        }

        function updateTransport(transport) {
        	//alert(transport);
          transports = (transport == 'all') ?  [] : [transport];
        }
        
        function log(message) {
            var console = document.getElementById('console');
            var p = document.createElement('p');
            p.style.wordWrap = 'break-word';
            p.appendChild(document.createTextNode(message));
            console.appendChild(p);
            while (console.childNodes.length > 25) {
                console.removeChild(console.firstChild);
            }
            console.scrollTop = console.scrollHeight;
        }
    </script>
</head>
<body>
<noscript><h2 style="color: #ff0000">Seems your browser doesn't support Javascript! Websockets 
    rely on Javascript being enabled. Please enable
    Javascript and reload this page!</h2></noscript>
<div>
    <div id="connect-container">
   		 username:<input id="username" type="text" name="username" ></input><br>
        <input id="radio1" type="radio"  name="group1" onclick="updateUrl('/BackServer/websocket');">
            <label for="radio1">W3C WebSocket</label>
        <br>
        <input id="radio2" type="radio" name="group1" onclick="updateUrl('/BackServer/websocket');">
            <label for="radio2">SockJS</label>
        <div id="sockJsTransportSelect" style="visibility:hidden;">
            <span>SockJS transport:</span>
            <select onchange="updateTransport(this.value)">
              <option value="all">all</option>
              <option value="websocket">websocket</option>
              <option value="xhr-polling">xhr-polling</option>
              <option value="jsonp-polling">jsonp-polling</option>
              <option value="xhr-streaming">xhr-streaming</option>
              <option value="iframe-eventsource">iframe-eventsource</option>
              <option value="iframe-htmlfile">iframe-htmlfile</option>
            </select>
        </div>
        	
        <div>
            <button id="connect" onclick="connect();">Connect</button>
            <button id="disconnect" disabled="disabled" onclick="disconnect();">Disconnect</button>
        </div>
        <div>
            <textarea id="message" style="width: 300px">Here is a message!</textarea>
        </div>
        <div>
            <button id="echo" onclick="echo();" disabled="disabled">Echo message</button>
            toUser<input type="text" id='toUser' name='toUser' value=''/>
        </div>
    </div>
    <div id="console-container">
        <div style=""><h1>message box</h1></div>
        <div id="console"></div>
    </div>
     <div style="float:left">
       	   <div style=" margin-left: 50px;"><h1>users online</h1></div>
          <div>  <textarea id="users" style=" margin-left: 50px;width: 550px;height:600px;float: left;"></textarea></div>
     </div>
</div>
<a href="<%=response.encodeURL("index.jsp;jsessionid=0CCD096E7F8D97B0BE608AFDC3E1931E?c=1&wd=Java") %>"> Homepage</a>
</body>
</html>