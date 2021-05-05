package com.uec.demo.bl;

import javax.websocket.Session;

public interface ChatRoomService {
    void speak(Session session, String roomName, String userName , String jsonStr);
    void ask(Session session, String roomName, String userName ,String jsonStr) throws InterruptedException;
    void answer(Session session, String roomName, String userName ,String jsonStr);
    void open(Session session, String roomName, String userName);
    void error(Throwable error);
    void close(String roomName,String userName);
}
