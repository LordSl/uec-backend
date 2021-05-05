package com.uec.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.uec.demo.util.BeanFetcher;
import com.uec.demo.util.GlobalLogger;
import com.uec.demo.util.WebSocketRouteResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Component
@ServerEndpoint(value = "/chat/{roomName}/{userName}")
public class ChatRoomController {

    static GlobalLogger logger;
    static WebSocketRouteResolver router;

    @Autowired
    public void init(BeanFetcher beanFetcher) {
        logger = beanFetcher.getBean(GlobalLogger.class);
        router = beanFetcher.getBean(WebSocketRouteResolver.class);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("roomName") String roomName, @PathParam("userName") String userName) throws Exception{
        router.proxy("open",session,roomName,userName);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("roomName") String roomName, @PathParam("userName") String userName, String jsonStr) throws Exception{
        logger.log(jsonStr);
        String type = JSONObject.parseObject(jsonStr).getString("type");
        router.proxy(type,session,roomName,userName,jsonStr);
    }

    @OnError
    public void onError(Throwable error) throws Exception{
        router.proxy("error",error);
    }

    @OnClose
    public void onClose(@PathParam("roomName") String roomName, @PathParam("userName") String userName) throws Exception{
        router.proxy("close",roomName,userName);
    }

}

