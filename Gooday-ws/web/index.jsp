<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>WebSocket 学习</title>
<script src="http://lib.sinaapp.com/js/jquery/1.7.2/jquery.min.js" type="text/javascript"></script>
<script type="text/javascript">
var socket = null;
var isLogin = false;
$(function(){

    // 聊天消息 指令
	$("#sendButton").click(function() {
		socket.send("message|"+$("#msg").val());
	});
	// 设置背景 指令
	$("#setBackground").click(function(){
		socket.send("background|"+$("#fillColor").val());
	});
	// 清屏
    $("#cleanButton").click(function(){
        $("#showMsg").html("");
    });

    // 登录或退出 操作
    $("#loginButton").click(function(){
        var _this = this;
        if(!isLogin){
			$.post("${pageContext.request.contextPath}/webapi/login",{"username":"dasheng"},function (result) {
				if(result.errorCode==0){
					$("#showMsg").append("登录成功...<br/>");
                    isLogin = true;
                    $(_this).val("退出");
				}else {
					$("#showMsg").html(result.errorMsg);
				}
			});
		}else{
            $.post("${pageContext.request.contextPath}/webapi/logout",null,function (result) {
                if(result.errorCode==0){
                    $("#showMsg").append("退出成功...<br/>");
                    isLogin = false;
                    $(_this).val("登录");
                }else {
                    $("#showMsg").html(result.errorMsg);
                }
            });
        }
    });
    // 创建连接
    $("#openButton").click(function(){
        //创建socket对象
        socket = new WebSocket("ws://"+ window.location.host+"/${pageContext.request.contextPath}socket");
        binding(socket);
    });
    // 退出连接
    $("#exitButton").click(function(){
        $("#showMsg").append("主动断开...<br/>");
        socket.close(1000, "不玩了");
    });
});

// 绑定socket事件
function binding(socket) {
    //连接创建后调用
    socket.onopen = function(evt) {
        console.log("onopen:" + evt);
        $("#showMsg").append("连接成功...<br/>");
    };
    //接收到服务器消息后调用
    socket.onmessage = function(message) {
        var data = null;
        try{
            data = parseObj(message.data);
        }catch(e){
            $("#showMsg").append("<span style='display:block;color:#FF0000'>server:"+message.data+"</span>");
            return;
        }
        if(data.type=="message"){
            $("#showMsg").append("server:"+data.text+"<br/>");
        }else if(data.type=="background"){
            $("#showMsg").append("改变当前背景色为:"+data.text+"<br/>");
            $("body").css("background-color",data.text);
        }
    };
    //关闭连接的时候调用
    socket.onclose = function(evt){
        console.log("close:" + evt);
        alert("close");
    };
    //出错时调用
    socket.onerror = function(evt) {
        console.log("error:" + evt);
        alert("error");
    };
}

//转换对象
function parseObj(strData){
    return (new Function( "return " + strData ))();
}
</script>
</head>
<body>
	<div id="showMsg" style="border: 1px solid; width: 500px; height: 400px; overflow: auto;"></div>
	<div>
		<input type="text" id="msg" /> 
		<input type="button" id="sendButton" value="发送" /><input type="button" id="cleanButton" value="清屏" />
		<input type="button" value="改背景" id="setBackground" /><input id="fillColor" type="color" />
		<input type="button" id="loginButton" value="登录" /><input type="button" id="openButton" value="连接" /><input type="button" id="exitButton" value="关闭" />
	</div>
</body>
</html>