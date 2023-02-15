package com.whaleswebrct.webrctdemo.Server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.util.JSONPObject;
import com.whaleswebrct.webrctdemo.message.JoinMessage;
import org.apache.tomcat.util.json.JSONParser;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Controller
@ServerEndpoint("/msgServer/{userId}")
@Component
@Scope("prototype")
public class WebSocketServer {

    /**
     * 静态变量，用来记录当前在线连接数。应该把它设计成线程安全的。
     */
    private static int onlineCount = 0;
    /**
     * concurrent包的线程安全Set，用来存放每个客户端对应的MyWebSocket对象。
     */
    private static ConcurrentHashMap<String, Session> webSocketMap = new ConcurrentHashMap<>();
    /**
     * 与某个客户端的连接会话，需要通过它来给客户端发送数据
     */
    private Session session;
    /**
     * 接收userId
     */
    private String userId = "";

    @OnOpen
    public void onOpen(Session session, @PathParam("userId") String userId) throws IOException {
        Enumeration<String> userKeys = webSocketMap.keys();
        List<String> userIdList = new ArrayList<>();

        JoinMessage joinMessage = new JoinMessage();
        joinMessage.setType("join");

        while (userKeys.hasMoreElements()) {
            userIdList.add(userKeys.nextElement());
        }
        joinMessage.setUserIdList(userIdList);
        System.out.println("在交流群中的用户：" + JSON.toJSONString(joinMessage));
        //把原有的userId发送出去
        session.getBasicRemote().sendText(JSON.toJSONString(joinMessage));
        this.session = session;
        this.userId = userId;
        /**
         * 连接被打开：向socket-map中添加session
         */
        webSocketMap.put(userId, session);
        System.out.println(userId + " - 连接建立成功...");
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        try {
            System.out.println("服务器接收到的消息：" + message);
            JSONObject jsonObject = JSONObject.parseObject(message);
            Object key = jsonObject.get("destinationId").toString();
            //判断用户是否还在线
            if (webSocketMap.get(key) == null) {
                webSocketMap.remove(key);
                System.err.println(key + " : null");
            }
            Session sessionValue = webSocketMap.get(key);
            //去除向自己转发
            if (key.equals(this.userId)) {
                return;
            }
            //判断session是否打开
            if (sessionValue.isOpen()) {
                System.out.println(key+"在线！");
                sessionValue.getBasicRemote().sendText(message);
            } else {
                System.err.println(key + ": not open");
                sessionValue.close();
                webSocketMap.remove(key);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        System.out.println("连接异常...");
        error.printStackTrace();
    }

    @OnClose
    public void onClose() {
        webSocketMap.remove(userId);
        System.out.println("连接关闭");
    }

    public static synchronized int getOnlineCount() {
        return onlineCount;
    }

    public static synchronized void addOnlineCount() {
        WebSocketServer.onlineCount++;
    }

    public static synchronized void subOnlineCount() {
        WebSocketServer.onlineCount--;
    }
}
