package com.goodayws.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;
import rxframework.utility.string.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

@Component
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler handler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            ServletServerHttpRequest serverHttpRequest = (ServletServerHttpRequest) request;
            HttpSession httpSession = serverHttpRequest.getServletRequest().getSession();
            if (httpSession != null) {
                System.out.println("HttpSessionId:"+httpSession.getId());
                attributes.put("currHttpSession",httpSession);

                String username = StringUtils.safeString(httpSession.getAttribute("username"));
                if(StringUtils.hasText(username)) {
                    attributes.put("username", username);
                }
            }else{
                System.out.println("httpsession is null");
            }
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}
