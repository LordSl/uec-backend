package com.uec.demo.blImpl;

import com.alibaba.fastjson.JSONObject;
import com.uec.demo.bl.ChatRoomService;
import com.uec.demo.config.SessionMapManager;
import com.uec.demo.util.GlobalJedis;
import com.uec.demo.util.GlobalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.util.HashMap;

@Component
public class ChatRoomServiceImpl implements ChatRoomService {
    HashMap<String, HashMap<String, Session>> rooms;
    @Autowired
    GlobalJedis jedis;
    @Autowired
    GlobalLogger logger;

    @Autowired
    public void init(SessionMapManager sessionMapManager){
        rooms = sessionMapManager.getRooms();
    }

    public void speak(Session session, String roomName, String userName ,String jsonStr){
        HashMap<String, Session> room = rooms.get(roomName);
        for (String u : room.keySet()) {
            Session s = room.get(u);
            reply(s, jsonStr);
        }
    }

    public void ask(Session session, String roomName, String userName ,String jsonStr) throws InterruptedException {
        //第2人...第n人加入房间时，向房间广播请求钥匙的信息
        HashMap<String, Session> room = rooms.get(roomName);
        String senderName = JSONObject.parseObject(jsonStr).getString("senderName");
        jedis.setnx("help:" + senderName, "", 60);

        String[] answerList = new String[room.size()];
        room.keySet().toArray(answerList);
        for (int i = room.size() - 1; i >= 0; i--) {
            boolean ok = jedis.setnx("help:" + senderName, "", 60);
            if (ok) break;
            else {
                String answerName = answerList[i];
                if (!answerName.equals(senderName)) {
                    Session s = room.get(answerName);
                    reply(s, jsonStr);
                    logger.log("send ask to answer " + answerName);
                }
            }
            Thread.sleep(1000);
        }
    }
    public void answer(Session session, String roomName, String userName ,String jsonStr) {
        String senderName = JSONObject.parseObject(jsonStr).getString("senderName");
        Session s = rooms.get(roomName).get(senderName);
        reply(s, jsonStr);
        jedis.del("help:" + senderName);
    }

    public void open(Session session, String roomName, String userName) {
        //房间默认存在时间为1天
        boolean ok = jedis.setnx("room:" + roomName, userName, 86400L);
        //websocket的session无法序列化，无法存进redis
        //这里只是用redis的原子性检验房间是否存在

        if (ok) {
            //创建者
            HashMap<String, Session> users = new HashMap<>();
            users.put(userName, session);
            rooms.put(roomName, users);
            logger.log("user " + userName + " create room " + roomName);
            reply(session, "master");
        } else {
            //加入者
            rooms.get(roomName).put(userName, session);
            logger.log("user " + userName + " join room " + roomName);
        }
    }

    public void error(Throwable error) {
        logger.error("error");
    }

    public void close(String roomName, String userName) {
        HashMap<String, Session> room = rooms.get(roomName);
        room.remove(userName);
        logger.log("user " + userName + " leave room " + roomName);
        if (room.size() == 0) {
            rooms.remove(roomName);
            logger.log("room " + roomName + " destroy");
            jedis.del("room:" + roomName);
        }
    }


    private void reply(Session session, String msg) {
        session.getAsyncRemote().sendText(msg);
    }
}
