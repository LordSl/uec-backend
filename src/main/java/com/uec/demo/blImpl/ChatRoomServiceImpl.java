package com.uec.demo.blImpl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.uec.demo.bl.ChatRoomService;
import com.uec.demo.config.SessionManager;
import com.uec.demo.util.GlobalJedis;
import com.uec.demo.util.GlobalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;

@Service
public class ChatRoomServiceImpl implements ChatRoomService {
    @Autowired
    SessionManager sessionManager;
    HashMap<String, HashMap<String, Session>> rooms;
    HashMap<String, HashMap<String, Integer>> dokis;
    @Autowired
    GlobalJedis jedis;
    @Autowired
    GlobalLogger logger;

    @Autowired
    public void init(SessionManager sessionManager) {
        rooms = sessionManager.getRooms();
        dokis = sessionManager.getDokis();
//        locks = sessionManager.getLocks();
    }

    public void speakResolve(Session session, String roomName, String userName, String jsonStr) {
        HashMap<String, Session> room = rooms.get(roomName);
        for (String u : room.keySet()) {
            Session s = room.get(u);
            reply(s, jsonStr);
        }
    }

    public void askResolve(Session session, String roomName, String userName, String jsonStr) throws InterruptedException {
        //第2人...第n人加入房间时，向房间广播请求钥匙的信息
        HashMap<String, Session> room = rooms.get(roomName);
        String senderName = getJsonProperty(jsonStr, "senderName");
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

    public void answerResolve(Session session, String roomName, String userName, String jsonStr) {
        String senderName = getJsonProperty(jsonStr, "senderName");
        Session s = rooms.get(roomName).get(senderName);
        reply(s, jsonStr);
        jedis.del("help:" + senderName);
    }

    public void openResolve(Session session, String roomName, String userName) throws IOException {
        //房间默认存在时间为1天
        boolean ok = jedis.setnx("room:" + roomName, userName, 86400L);
        //websocket的session无法序列化，无法存进redis
        //这里只是用redis的原子性检验房间是否存在

        if (ok) {
            //创建者
            HashMap<String, Session> users = rooms.merge(roomName, new HashMap<>(), (oldValue, newValue) -> oldValue);
            users.put(userName, session);
            logger.log("user " + userName + " create room " + roomName);

            reply(session, replyText("answer", "master"));

            sessionManager.startListenDoki(roomName);

        } else {
            //加入者
            HashMap<String, Session> room = rooms.get(roomName);
            if (room.containsKey(userName)) {
                session.close();
                return;
            }

            room.put(userName, session);

            for (Session s : room.values())
                reply(s, replyText("notice", userName + "进入房间"));

            logger.log("user " + userName + " join room " + roomName);
        }
    }

    @Override
    public void dokiResolve(Session session, String roomName, String userName, String jsonStr) {
        HashMap<String, Integer> room = dokis.merge(roomName, new HashMap<>(), (oldValue, newValue) -> oldValue);
        room.merge(userName, 1, (oldValue, newValue) -> newValue);
        logger.log("user " + userName + " in room " + roomName + " live");
    }

    public void errorResolve(Throwable error) {
        logger.error("error");
    }

    public void closeResolve(String roomName, String userName) {
        HashMap<String, Session> room = rooms.get(roomName);
        room.remove(userName);

        for (Session s : room.values())
            reply(s, replyText("notice", userName + "离开房间"));

        logger.log("user " + userName + " leave room " + roomName);
        if (room.size() == 0) {
            rooms.remove(roomName);
            dokis.remove(roomName);
            sessionManager.stopListenDoki(roomName);
            logger.log("room " + roomName + " destroy");
            jedis.del("room:" + roomName);
        }

    }


    private void reply(Session session, String msg) {
        session.getAsyncRemote().sendText(msg);
    }

    private String replyText(String type, String content) {
        JSONObject jo = new JSONObject();
        jo.put("type", type);
        jo.put("content", content);
        return jo.toJSONString();
    }

    private String getJsonProperty(String jsonStr, String property) {
        JSONObject jo = JSON.parseObject(jsonStr);
        return jo.getString(property);
    }
}
