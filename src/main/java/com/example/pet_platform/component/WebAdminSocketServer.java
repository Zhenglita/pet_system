package com.example.pet_platform.component;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.example.pet_platform.controller.util.CensorResult;
import com.example.pet_platform.entity.Message;
import com.example.pet_platform.entity.User;
import com.example.pet_platform.service.BaiduContentCensorService;
import com.example.pet_platform.service.MessageService;
import com.example.pet_platform.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@ServerEndpoint(value = "/adminserver/{username}")
@Component
public class WebAdminSocketServer {
    private static ApplicationContext applicationContext;

    public static void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

    //饿汉加载
    private static BaiduContentCensorService baiduContentCensorService;
    @Autowired
    public void setBaiduContentCensorService(BaiduContentCensorService baiduContentCensorService){
        WebAdminSocketServer.baiduContentCensorService=baiduContentCensorService;
    }
    private static final Logger log = LoggerFactory.getLogger(WebSocketServer.class);

    /**
     * 记录当前在线连接数
     */
    public static final Map<String, Session> sessionMap = new ConcurrentHashMap<>();

    /**
     * 连接建立成功调用的方法
     */
    @OnOpen
    public void onOpen(Session session, @PathParam("username") String username) {
        sessionMap.put(username, session);
        log.info("有新用户加入，username={}, 当前在线人数为：{}", username, sessionMap.size());
        JSONObject result = new JSONObject();
        JSONArray array = new JSONArray();
        result.set("users", array);
        for (Object key : sessionMap.keySet()) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.set("username", key);
            array.add(jsonObject);
        }
        sendAllMessage(JSONUtil.toJsonStr(result));  // 后台发送消息给所有的客户端
    }

    /**
     * 连接关闭调用的方法
     */
    @OnClose
    public void onClose(@PathParam("username") String username) {
        sessionMap.remove(username);
        log.info("有一连接关闭，移除username={}的用户session, 当前在线人数为：{}", username, sessionMap.size());
    }



    /**
     * 收到客户端消息后调用的方法
     * 后台收到客户端发送过来的消息
     * onMessage 是一个消息的中转站
     * 接受 浏览器端 socket.send 发送过来的 json数据
     * @param message 客户端发送过来的消息
     */
    @OnMessage
    public void onMessage(String message, Session session, @PathParam("username") String username) {
        log.info("服务端收到用户username={}的消息:{}", username, message);
        JSONObject obj = JSONUtil.parseObj(message);
        String toUsername = obj.getStr("to"); // to表示发送给哪个用户，比如 admin
        String text = obj.getStr("text"); // 发送的消息文本  hello
        Message message1 = new Message();
        message1.setUsername(username);
        message1.setToUsername(toUsername);
        CensorResult result = baiduContentCensorService.getCommonTextCensorResult(text);
        Map map = com.alibaba.fastjson.JSONObject.parseObject(com.alibaba.fastjson.JSONObject.toJSONString(com.alibaba.fastjson.JSONObject.parseObject(result.getTextCensorJson())), Map.class);
        System.out.println(map.get("data"));
        if (map.get("data")!=null){
            int length = text.length();
            StringBuilder x= new StringBuilder();
            for(int i=0;i<length;i++){
                x.append("*");
            }
            System.err.println();
            text=x.toString();
            message1.setMessage(x.toString());
        }else {
            message1.setMessage(text);
        }
        UserService bean = applicationContext.getBean(UserService.class);
        QueryWrapper<User> qw=new QueryWrapper<>();
        qw.eq("username",username);
        User one = bean.getOne(qw);
        message1.setFrom_uid(one.getUid());
        QueryWrapper<User> qw1=new QueryWrapper<>();
        qw1.eq("username",toUsername);
        User one1 = bean.getOne(qw1);
        message1.setTo_uid(one1.getUid());
        MessageService bean1 = applicationContext.getBean(MessageService.class);
        bean1.save(message1);
        // {"to": "admin", "text": "聊天文本"}
        Session toSession = sessionMap.get(toUsername); // 根据 to用户名来获取 session，再通过session发送消息文本
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("from", username);  // from 是 zhang
        jsonObject.set("text", text);  // text 同上面的text
        if (toSession != null) {
            this.sendMessage(jsonObject.toString(), toSession);
            log.info("发送给用户username={}，消息：{}", toUsername, jsonObject.toString());
        } else {
            jsonObject.set("text",false);
            this.sendMessage(jsonObject.toString(),session);
            log.info("发送失败，未找到用户username={}的session", toUsername);
        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
        log.error("发生错误");
        error.printStackTrace();
    }

    /**
     * 服务端发送消息给客户端
     */
    private void sendMessage(String message, Session toSession) {
        try {
            log.info("服务端给客户端[{}]发送消息{}", toSession.getId(), message);
            toSession.getBasicRemote().sendText(message);
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }

    /**
     * 服务端发送消息给所有客户端
     */
    private void sendAllMessage(String message) {
        try {
            for (Session session : sessionMap.values()) {
                log.info("服务端给客户端[{}]发送消息{}", session.getId(), message);
                Map<String, String> map = new HashMap<>();
                map.put("sessionId", session.getId());
                map.put("message", message);
                session.getBasicRemote().sendText(message);
            }
        } catch (Exception e) {
            log.error("服务端发送消息给客户端失败", e);
        }
    }





}
