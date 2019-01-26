package com.gemini.websocketdemo01.websocket;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.*;

import java.io.IOException;
import java.util.*;

import org.apache.commons.logging.Log;

/**
 * 处理websocket消息
 *
 * @author LiQun
 * @date 2019/1/22
 */
@Service
public class MyHandler implements WebSocketHandler {

    private static Log logger = LogFactory.getLog(MyHandler.class);

    /**
     * 在线用户,将其保存在set中,避免用户重复登录,出现多个session
     */
    private static final Map<String, WebSocketSession> USERS;

    static {
        USERS = Collections.synchronizedMap(new HashMap<>());
    }

    private static final String SEND_ALL = "all";


    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        System.out.println("链接成功.....");
        logger.info("afterConnectionEstablished");
        String id = (String) session.getAttributes().get("WEBSOCKET_USER_ID");
        logger.info("用户id:" + id);
        if (id != null) {
            USERS.put(id, session);
            JSONObject obj = new JSONObject();
            // 统计一下当前登录系统的用户有多少个
            obj.put("count", USERS.size());
            obj.put("users", USERS.keySet().toArray());
            session.sendMessage(new TextMessage(obj.toJSONString()));
        }
    }

    @Override
    public void handleMessage(WebSocketSession session, WebSocketMessage<?> message) throws Exception {
        JSONObject msg = JSON.parseObject(message.getPayload().toString());
        logger.info("处理要发送的消息:" + message.getPayload().toString());
        JSONObject obj = new JSONObject();
        String type = msg.get("type").toString();
        if (StringUtils.isNotBlank(type) && SEND_ALL.equals(type)) {
            //给所有人
            obj.put("msg", msg.getString("msg"));
            logger.info("给所有人发消息");
            sendMessageToUsers(new TextMessage(obj.toJSONString()));
        } else {
            //给个人
            String to = msg.getString("to");
            obj.put("msg", msg.getString("msg"));
            logger.info("给个人发消息");
            sendMessageToUser(to, new TextMessage(obj.toJSONString()));
        }
    }

    @Override
    public void handleTransportError(WebSocketSession session, Throwable exception) throws Exception {
        if (session.isOpen()) {
            session.close();
        }
        logger.info("链接出错，关闭链接,异常信息:" + exception.getMessage());
        String userId = getUserId(session);
        if (USERS.get(userId) != null) {
            USERS.remove(userId);
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus closeStatus) throws Exception {
        logger.info("链接关闭,关闭信息:" + closeStatus.toString());
        String userId = getUserId(session);
        if (USERS.get(userId) != null) {
            USERS.remove(userId);
        }
    }

    @Override
    public boolean supportsPartialMessages() {
        return false;
    }

    /**
     * 获取用户id
     *
     * @author LiQun
     * @date 2019/1/22
     */
    private String getUserId(WebSocketSession session) {
        try {
            return (String) session.getAttributes().get("WEBSOCKET_USER_ID");
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 给所有在线用户发送消息
     * @param message 文本消息
     */
    public void sendMessageToUsers(TextMessage message) {
        WebSocketSession user = null;
        for (String key : USERS.keySet()) {
            user = USERS.get(key);
            try {
                if (user.isOpen()) {
                    user.sendMessage(message);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 给某个用户发送消息
     *
     * @param userName 用户id
     * @param message  消息
     */
    public void sendMessageToUser(String userName, TextMessage message) {
        WebSocketSession user = USERS.get(userName);
        try {
            if (user.isOpen()) {
                user.sendMessage(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 根据当前用户id登出
     *
     * @author LiQun
     * @date 2019/1/22
     * @param userId 用户id
     */
    public void logout(String userId) {
        USERS.remove(userId);
        logger.info("用户登出,id:" + userId);
    }
}