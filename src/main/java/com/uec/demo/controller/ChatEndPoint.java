package com.uec.demo.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uec.demo.util.GlobalLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@ServerEndpoint(value = "/chat/{userName}")
public class ChatEndPoint {

    @Autowired
    GlobalLogger logger;
    @Autowired
    Jedis jedis;

    /**
     * 建立连接
     */
    @OnOpen
    public void onOpen(@PathParam("userId") String userId, Session session)
    {
//        onlineNumber+=1;
//        logger.info("现在来连接的客户id："+session.getId()+"用户名："+userId);
//        this.userId = userId;
//        this.session = session;
//        //  logger.info("有新连接加入！ 当前在线人数" + onlineNumber);
//        try {
//            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
//            //先给所有人发送通知，说我上线了
//            JSONObject jo = new JSONObject();
//            jo.put("messageType",1);
//            jo.put("userId",userId);
//            sendMessageAll(jo.toJSONString(),userId);
//
//            //把自己的信息加入到map当中去
//            clients.put(userId, this);
//            logger.info("当前在线人数" + clients.size());
//            //给自己发一条消息：告诉自己现在都有谁在线
//            JSONObject jojo = new JSONObject();
//            jojo.put("messageType",3);
//            jojo.put("onlineUsers",clients.keySet());
//            sendMessageTo(jojo.toJSONString(),userId);
//        }
//        catch (IOException e){
//            logger.info(userId+"上线的时候通知所有人发生了错误");
//        }
    }

    @OnError
    public void onError(Session session, Throwable error) {
//        logger.info("服务端发生了错误"+error.getMessage());
    }
    /**
     * 连接关闭
     */
    @OnClose
    public void onClose()
    {
//        onlineNumber--;
//        clients.remove(userId);
//        try {
//            //messageType 1代表上线 2代表下线 3代表在线名单  4代表普通消息
//            JSONObject jo = new JSONObject();
//            jo.put("messageType",2);
//            jo.put("onlineUsers",clients.keySet());
//            jo.put("userId",userId);
//            sendMessageAll(jo.toJSONString(),userId);
//        }
//        catch (IOException e){
//            logger.info(userId+"下线的时候通知所有人发生了错误");
//        }
//        logger.info("有连接关闭！ 当前在线人数" + clients.size());
    }

    /**
     * 收到客户端的消息
     * 这里的message可以理解成代指任何交互
     */
    @OnMessage
    public void onMessage(String message, Session session)
    {
//        try {
//            logger.info("来自客户端消息：" + message+"客户端的id是："+session.getId());
//
//            System.out.println("------------  :"+message);
//
//            JSONObject jsonObject = JSON.parseObject(message);
//            String textMessage = jsonObject.getString("message");
//            String fromUserId = jsonObject.getString("userId");
//            String toUserId = jsonObject.getString("to");
//
//            //如果不是发给所有，那么就发给某一个人
//            //messageType 1代表上线 2代表下线 3代表在线名单 4代表普通消息
//            JSONObject jo = new JSONObject();
//            jo.put("messageType",4);
//            jo.put("textMessage",textMessage);
//            jo.put("fromUserId",fromUserId);
//            if(toUserId.equals("All")){
//                jo.put("toUserId","所有人");
//                sendMessageAll(jo.toJSONString(),fromUserId);
//            }
//            else{
//                jo.put("toUserId",toUserId);
//                System.out.println("开始推送消息给"+toUserId);
//                sendMessageTo(jo.toJSONString(),toUserId);
//            }
//        }
//        catch (Exception e){
//            e.printStackTrace();
//            logger.info("发生错误了");
//        }

    }
}

