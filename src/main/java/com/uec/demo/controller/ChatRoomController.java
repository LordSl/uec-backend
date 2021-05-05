package com.uec.demo.controller;

import com.uec.demo.util.BeanFetcher;
import com.uec.demo.util.GlobalLogger;
import com.uec.demo.util.WebSocketRouteResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint(value = "/chat/{type}/{roomName}/{userName}")
public class ChatRoomController {

    static GlobalLogger logger;
    static WebSocketRouteResolver router;

    @Autowired
    public void init(BeanFetcher beanFetcher) {
        logger = beanFetcher.getBean(GlobalLogger.class);
        router = beanFetcher.getBean(WebSocketRouteResolver.class);
    }

    @OnOpen
    public void onOpen(@PathParam("type") String type, Session session, @PathParam("roomName") String roomName, @PathParam("userName") String userName) throws Exception{
        router.proxy(type,session,roomName,userName);
    }

    @OnMessage
    public void onMessage(@PathParam("type") String type, Session session, @PathParam("roomName") String roomName, @PathParam("userName") String userName, String jsonStr) throws Exception{
        logger.log(jsonStr);
        router.proxy(type,session,roomName,userName,jsonStr);
    }

    @OnError
    public void onError(@PathParam("type") String type, Throwable error) throws Exception{
        router.proxy(type,error);
    }

    @OnClose
    public void onClose(@PathParam("type") String type, @PathParam("roomName") String roomName, @PathParam("userName") String userName) throws Exception{
        router.proxy(type,roomName,userName);
    }

}

