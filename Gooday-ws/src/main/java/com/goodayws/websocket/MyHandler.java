package com.goodayws.websocket;

import com.goodaybase.constant.CommParams;
import com.goodaybase.model.entity.GpsPoint;
import net.sf.json.JSONObject;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import rxframework.utility.string.StringUtils;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.*;

@Service
public class MyHandler extends TextWebSocketHandler {
    //在线用户列表
    private static final Map<String, WebSocketSession> users;
    //用户标识列表
    private static final Map<String, String> devices;
    //用户标识
    private static final String CLIENT_ID = "username";

    static {
        users = new HashMap<String, WebSocketSession>();
    }

    static {
        devices = new HashMap<String, String>();
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("成功建立连接");
        users.put(session.getId(), session);
    }

    /**
     * 接收消息
     *
     * @param session
     * @param message
     */
    @Override
    public void handleTextMessage(WebSocketSession session, TextMessage message) throws IOException {

        String msg = message.getPayload();
        System.out.println("收到：" + msg);

        try {
            JSONObject jsonObj = JSONObject.fromObject(msg);
            String cmd = StringUtils.safeString(jsonObj.get("cmd"));
            String data = StringUtils.safeString(jsonObj.get("data"));
            switch (cmd) {
                case "CMD_LOGIN":
                    // 响应指令
                    JSONObject loginResp = new JSONObject();
                    loginResp.put("cmd", "CMD_LOGIN_RESP");

                    JSONObject json = JSONObject.fromObject(data);
                    String username = StringUtils.safeString(json.get("username"));
                    if (StringUtils.hasText(username)) {
                        devices.put(session.getId(), username);

                        loginResp.put("data", "登录成功");
                        session.sendMessage(new TextMessage(loginResp.toString()));

                        System.out.println("登录成功");
                    } else {
                        loginResp.put("data", "身份验证失败");
                        session.sendMessage(new TextMessage(loginResp.toString()));

                        session.close(new CloseStatus(1002, "身份验证失败"));
                    }
                    break;
                case "CMD_MSG":
                    // 响应指令
                    JSONObject msgResp = new JSONObject();
                    msgResp.put("cmd", "CMD_MSG_RESP");

                    username = StringUtils.safeString(devices.get(session.getId()));
                    if (StringUtils.hasText(username)) {
                        msgResp.put("data", "消息已收到");
                        session.sendMessage(new TextMessage(msgResp.toString()));
                    } else {
                        msgResp.put("data", "身份验证失败");
                        session.sendMessage(new TextMessage(msgResp.toString()));

                        session.close(new CloseStatus(1002, "身份验证失败"));
                    }
                    break;
                case "CMD_LOCATE":
                    // 响应指令
                    JSONObject locateResp = new JSONObject();
                    locateResp.put("cmd", "CMD_LOCATE_RESP");

                    username = StringUtils.safeString(devices.get(session.getId()));
                    if (StringUtils.hasText(username)) {
                        locateResp.put("data", "定位已收到");
                        session.sendMessage(new TextMessage(locateResp.toString()));

                        // 处理定位点数据
                        handleLocationPoints(data);
                    } else {
                        locateResp.put("data", "身份验证失败");
                        session.sendMessage(new TextMessage(locateResp.toString()));

                        session.close(new CloseStatus(1002, "身份验证失败"));
                    }
                    break;

                /*以下为网页调试指令 */
                case "CMD_WEB_MSG":
                    // 响应指令
                    JSONObject webMsgResp = new JSONObject();
                    webMsgResp.put("cmd", "CMD_WEB_MSG_RESP");
                    webMsgResp.put("data", data);
                    session.sendMessage(new TextMessage(webMsgResp.toString()));
                    break;
                case "CMD_WEB_BG":
                    // 响应指令
                    JSONObject webBgResp = new JSONObject();
                    webBgResp.put("cmd", "CMD_WEB_BG_RESP");
                    webBgResp.put("data", data);
                    session.sendMessage(new TextMessage(webBgResp.toString()));
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
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
        devices.remove(session.getId());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        System.out.println("连接已关闭：" + status);
        devices.remove(getClientId(session));
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

    /**
     * 处理定位点数据
     */
    private void handleLocationPoints(String data) {
        if (StringUtils.isNullOrEmpty(data)) {
            return;
        }
        Queue<GpsPoint> queue = new LinkedList<GpsPoint>();
        // Object objLocationPoints = MCacheUtils.get(WebCache.getSystemParam("PLATFORM_NAMESPACE") + "_LOCATION_POINTS", CLIENT_NAME);
        // Object objLocationPoints = SessionContext.getSession().getAttribute(CLIENT_NAME);
        Object objLocationPoints = CommParams.queues.get(CommParams.CLIENT_NAME);
        if (objLocationPoints != null) {
            queue = (Queue<GpsPoint>) objLocationPoints;
        }
        String[] split = data.replace(":", ",").split(",");
        double lng = Double.parseDouble(split[1]);
        double lat = Double.parseDouble(split[2]);
        GpsPoint point = new GpsPoint(lng, lat);
        queue.offer(point);
        if (queue.size() > 100) {
            queue.poll();
        }

        //MCacheUtils.put(WebCache.getSystemParam("PLATFORM_NAMESPACE") + "_LOCATION_POINTS", CLIENT_NAME, queue);
        CommParams.queues.put(CommParams.CLIENT_NAME, queue);
        // queues.put(CLIENT_NAME, queue);
        System.out.println("queueSize = " + queue.size());
    }
}