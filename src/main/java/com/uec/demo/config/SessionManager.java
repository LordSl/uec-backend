package com.uec.demo.config;

import com.uec.demo.util.GlobalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.io.IOException;
import java.util.HashMap;

@Component
public class SessionManager {
    HashMap<String, HashMap<String, Session>> rooms;
    HashMap<String, HashMap<String, Integer>> dokis;
    HashMap<String, Thread> tPool;
    @Autowired
    GlobalLogger logger;

    public SessionManager() {
        rooms = new HashMap<>();
        dokis = new HashMap<>();
        tPool = new HashMap<>();
    }

    public HashMap<String, HashMap<String, Session>> getRooms() {
        return rooms;
    }

    public HashMap<String, HashMap<String, Integer>> getDokis() {
        return dokis;
    }


    public void startListenDoki(String roomName) {
        Thread t = new Thread(() -> {
            try {
                while (!Thread.interrupted()) {
                    Thread.sleep(10000);
                    HashMap<String, Integer> room = dokis.get(roomName);
                    for (String userName : room.keySet()) {
                        if (room.get(userName) == 0) {
                            logger.log("user " + userName + " in room " + roomName + " dead");
                            if (rooms.get(roomName).get(userName) != null) rooms.get(roomName).get(userName).close();
                        }
                        dokis.get(roomName).replace(userName, 0);
                    }
                    logger.log("doki check");

                }
            } catch (InterruptedException e) {
                logger.log("listen doki " + roomName + " finish");
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        tPool.put(roomName, t);
        t.start();
    }

    public void stopListenDoki(String roomName) {
        Thread t = tPool.get(roomName);
        t.interrupt();
        tPool.remove(roomName);
    }
}
