package com.uec.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.uec.demo.util.BeanFetcher;
import com.uec.demo.util.GlobalLogger;
import com.uec.demo.util.WSResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

@Controller
@ServerEndpoint(value = "/chat/{roomName}/{userName}")
public class ChatRoomController {

    static GlobalLogger logger;
    static WSResolver resolver;
    //这里使用代理-反射，而不是直接用BeanFetcher找对应接口的service实现类
    //主要是为了避免在代码中出现大量的if-else，方便自动化配置
    //当然，也因此失去了用接口对齐参数的机会

    @Autowired
    public void init(BeanFetcher beanFetcher) {
        logger = beanFetcher.getBean(GlobalLogger.class);
        resolver = beanFetcher.getBean(WSResolver.class);
    }

    @OnOpen
    public void onOpen(Session session, @PathParam("roomName") String roomName, @PathParam("userName") String userName) throws Exception{
        resolver.proxy("open",session,roomName,userName);
    }

    @OnMessage
    public void onMessage(Session session, @PathParam("roomName") String roomName, @PathParam("userName") String userName, String jsonStr) throws Exception{
        logger.log(jsonStr);
        String type = JSONObject.parseObject(jsonStr).getString("type");
        resolver.proxy(type,session,roomName,userName,jsonStr);
    }

    @OnError
    public void onError(Throwable error) throws Exception{
        resolver.proxy("error",error);
    }

    @OnClose
    public void onClose(@PathParam("roomName") String roomName, @PathParam("userName") String userName) throws Exception{
        resolver.proxy("close",roomName,userName);
    }

}

