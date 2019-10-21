package com.goodayws.websocket;

import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rxframework.utility.string.StringUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class MyHandler extends TextWebSocketHandler {
    //在线用户列表
    private static final Map<String, WebSocketSession> users;
    //用户标识
    private static final String CLIENT_ID = "username";

    static {
        users = new HashMap<String, WebSocketSession>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("成功建立连接");
        String username = getClientId(session);
        System.out.println("username = " + username);
        if (StringUtils.hasText(username)) {
            users.put(username, session);
            session.sendMessage(new TextMessage("成功建立socket连接"));
            System.out.println(session);
        }else{
            session.sendMessage(new TextMessage("身份验证失败"));
            System.out.println(session);
            session.close(new CloseStatus(1002, "身份验证失败"));
        }
    }

    /**
     * 接收消息
     *
     * @param session
     * @param message
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) {

        String msg = message.getPayload();
        System.out.println(msg);

        // 处理消息
        if (msg.indexOf("message|") == 0) {
            String data = msg.replace("message|", "");
            msg = "{\"type\":\"message\",\"text\":\"" + data + "\"}";
        } else if (msg.indexOf("background|") == 0) {
            String data = msg.replace("background|", "");
            msg = "{\"type\":\"background\",\"text\":\"" + data + "\"}";
        }

        WebSocketMessage message1 = new TextMessage(msg);
        try {
            session.sendMessage(message1);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送信息给指定用户
     *
     * @param clientId
     * @param message
     * @return
     */
    public boolean sendMessageToUser(String clientId, TextMessage message) {
        if (users.get(clientId) == null) {
            System.out.println("clientId为空");
            return false;
        }
        WebSocketSession session = users.get(clientId);
        System.out.println("sendMessage:" + session);
        if (!session.isOpen()) {
            System.out.println("clientId=" + clientId + ",会话未打开或中断");
            return false;
        }
        try {
            session.sendMessage(message);
        } catch (IOException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }

    /**
     * 广播信息
     *
     * @param message
     * @return
     */
    public boolean sendMessageToAllUsers(TextMessage message) {
        boolean allSendSuccess = true;
        Set<String> clientIds = users.keySet();
        WebSocketSession session = null;
        for (String clientId : clientIds) {
            try {
                session = users.get(clientId);
                if (session.isOpen()) {
                    session.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
                allSendSuccess = false;
            }
        }

        return allSendSuccess;
    }


    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        System.out.println("连接出错");
        users.remove(getClientId(session));
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("连接已关闭：" + status);
        users.remove(getClientId(session));
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户标识
     *
     * @param session
     * @return
     */
    private String getClientId(WebSocketSession session) {
        try {
            Map<String, Object> attributes = session.getAttributes();
            HttpSession httpSession = (HttpSession) attributes.get("currHttpSession");
            String clientId = StringUtils.safeString(httpSession.getAttribute(CLIENT_ID));
            return clientId;
        } catch (Exception e) {
            return null;
        }
    }
}