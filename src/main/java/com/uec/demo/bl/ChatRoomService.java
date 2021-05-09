package com.uec.demo.bl;

import javax.websocket.Session;
import java.io.IOException;

public interface ChatRoomService {
    void speakResolve(Session session, String roomName, String userName, String jsonStr);

    void askResolve(Session session, String roomName, String userName, String jsonStr) throws InterruptedException;

    void answerResolve(Session session, String roomName, String userName, String jsonStr);

    void dokiResolve(Session session, String roomName, String userName, String jsonStr);

    void openResolve(Session session, String roomName, String userName) throws IOException;

    void errorResolve(Throwable error);

    void closeResolve(String roomName, String userName);
}
