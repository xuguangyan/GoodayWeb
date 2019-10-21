package com.goodayws.websocket;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;

public class TestMain {

    @Test
    public void test01(){
        try {
            URI uri = new URI("ws://localhost:8018/socket");
            MyWebSocketClient myClient = new MyWebSocketClient(uri);
            myClient.connect();
            // 往websocket服务端发送数据
            myClient.send("此为要发送的数据内容");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }
}
