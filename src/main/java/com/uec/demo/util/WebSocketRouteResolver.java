package com.uec.demo.util;

import com.uec.demo.blImpl.ChatRoomServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@Component
public class WebSocketRouteResolver {
    HashMap<String,Method> routeMap;
    @Autowired
    BeanFetcher beanFetcher;
//    String configJsonFilePath = "src/main/resources/wsroute.json";

    public WebSocketRouteResolver() throws NoSuchMethodException {
        routeMap = new HashMap<>();
//        String jsonStr = GlobalTrans.getJsonString(configJsonFilePath);
        routeMap.put("speak", ChatRoomServiceImpl.class.getDeclaredMethod("speak", Session.class, String.class, String.class, String.class));
        routeMap.put("ask", ChatRoomServiceImpl.class.getDeclaredMethod("ask", Session.class, String.class, String.class, String.class));
        routeMap.put("answer", ChatRoomServiceImpl.class.getDeclaredMethod("answer", Session.class, String.class, String.class, String.class));
        routeMap.put("open", ChatRoomServiceImpl.class.getDeclaredMethod("open", Session.class, String.class, String.class));
        routeMap.put("close", ChatRoomServiceImpl.class.getDeclaredMethod("close", String.class, String.class));
        routeMap.put("error", ChatRoomServiceImpl.class.getDeclaredMethod("error", Throwable.class));

    }

    public void proxy(String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method method = routeMap.get(methodName);
        Class<?> c = method.getDeclaringClass();
        Object instance = beanFetcher.getBean(c);
        method.invoke(c,args);
    }
}
