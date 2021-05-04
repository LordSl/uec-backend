package com.uec.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.uec.demo.util.BeanFetcher;
import com.uec.demo.util.GlobalLogger;
import com.uec.demo.util.GlobalJedis;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.util.HashMap;

@Component
@ServerEndpoint(value = "/chat/{roomName}/{userName}")
public class ChatRoomEndPoint {

    GlobalLogger logger = new GlobalLogger();
    GlobalJedis globalJedis = new GlobalJedis();

    static HashMap<String,HashMap<String,Session>> rooms = new HashMap<>();

    @OnOpen()
    public void onOpen(Session session, @PathParam("roomName")String roomName, @PathParam("userName")String userName) {
        //房间默认存在时间为1天
        boolean ok = globalJedis.setnx("room:"+roomName, userName, 86400L);
        //websocket的session无法序列化，无法存进redis
        //这里只是用redis的原子性检验房间是否存在

        if(ok){
            //创建者
            HashMap<String,Session> users = new HashMap<>();
            users.put(userName,session);
            rooms.put(roomName,users);
            logger.log("user "+userName+" create room "+roomName);
            reply(session,"master");
        }
        else {
            //加入者
            rooms.get(roomName).put(userName,session);
            logger.log("user "+userName+" join room "+roomName);
        }
    }

    @OnMessage()
    public void onMessage(Session session, @PathParam("roomName")String roomName, @PathParam("userName")String userName, String jsonStr) throws Exception {
        logger.log(jsonStr);

        //第2人...第n人加入房间时，向房间广播请求钥匙的信息
        //jsonStr -> modulus exponent senderName
        JSONObject jo = JSONObject.parseObject(jsonStr);
        String senderName = jo.getString("senderName");
        String type = jo.getString("type");

        if(type.equals("speak")){
            HashMap<String, Session> room = rooms.get(roomName);
            for (String u : room.keySet()) {
                Session s = room.get(u);
                reply(s, jsonStr);
            }
        }

        if(type.equals("ask")) {
            HashMap<String, Session> room = rooms.get(roomName);
            for (String u : room.keySet()) {
//                boolean ok = globalJedis.setnx("help:"+senderName,"",60);
//                if(ok) break;
//                else {
                    if (!u.equals(senderName)) {
                        logger.log(u);
                        Session s = room.get(u);
                        reply(s, jsonStr);
                    }
//                }
//                Thread.sleep(1000);
            }
        }

        if(type.equals("answer")) {
            Session s = rooms.get(roomName).get(senderName);
            reply(s, jsonStr);
            globalJedis.del("help:"+senderName);
        }
    }

    @OnError
    public void onError(Throwable error) {
        logger.error("error");
    }

    @OnClose
    public void onClose(@PathParam("roomName")String roomName, @PathParam("userName") String userName) {
        HashMap<String, Session> room = rooms.get(roomName);
        room.remove(userName);
        logger.log("user "+userName+" leave room "+roomName);
        if(room.size()==0){
            rooms.remove(roomName);
            logger.log("room "+roomName+" destroy");
            globalJedis.del("room:"+roomName);
        }
    }

    private void reply(Session session,String msg){
        session.getAsyncRemote().sendText(msg);
    }

}

