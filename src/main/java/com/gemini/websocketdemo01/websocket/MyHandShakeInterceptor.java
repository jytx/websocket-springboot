package com.gemini.websocketdemo01.websocket;

import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * websocket消息拦截器
 * @author LiQun
 * @date 2019/1/22
 */
@Service
public class MyHandShakeInterceptor implements HandshakeInterceptor {
    @Override
    public boolean beforeHandshake(
            ServerHttpRequest request,
            ServerHttpResponse response, WebSocketHandler webSocketHandler, Map<String, Object> attributes) throws Exception {
        if (request instanceof ServletServerHttpRequest) {
            HttpServletRequest servletRequest = ((ServletServerHttpRequest) request).getServletRequest();
            String id = request.getURI().toString().split("id=")[1];
            System.out.println("当前session的ID=" + id);
//             从session中获取到当前登录的用户信息. 作为socket的账号信息. session的的WEBSOCKET_USER_ID信息,在用户打开页面的时候设置.
            attributes.put("WEBSOCKET_USER_ID", id);
        }
        return true;
    }

    @Override
    public void afterHandshake(ServerHttpRequest serverHttpRequest,
                               ServerHttpResponse serverHttpResponse, WebSocketHandler webSocketHandler, Exception e) {

    }
}