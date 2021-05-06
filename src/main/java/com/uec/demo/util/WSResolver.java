package com.uec.demo.util;

import com.uec.demo.bl.ChatRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.Session;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@Component
public class WSResolver {
    HashMap<String,Method> routeMap;
    @Autowired
    BeanFetcher beanFetcher;
//    String configJsonFilePath = "src/main/resources/wsroute.json";
    //todo 目前还是手工注入，之后会改成json或xml注入

    public WSResolver() throws NoSuchMethodException {
        routeMap = new HashMap<>();
//        String jsonStr = GlobalTrans.getJsonString(configJsonFilePath);
        routeMap.put("speak", ChatRoomService.class.getDeclaredMethod("speak", Session.class, String.class, String.class, String.class));
        routeMap.put("ask", ChatRoomService.class.getDeclaredMethod("ask", Session.class, String.class, String.class, String.class));
        routeMap.put("answer", ChatRoomService.class.getDeclaredMethod("answer", Session.class, String.class, String.class, String.class));
        routeMap.put("open", ChatRoomService.class.getDeclaredMethod("open", Session.class, String.class, String.class));
        routeMap.put("close", ChatRoomService.class.getDeclaredMethod("close", String.class, String.class));
        routeMap.put("error", ChatRoomService.class.getDeclaredMethod("error", Throwable.class));

    }

    public void proxy(String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        Method method = routeMap.get(methodName);
        Class<?> c = method.getDeclaringClass();
        Object instance;
        if(c.isInterface()) instance = beanFetcher.getBeanOfType(c);
        else  instance = beanFetcher.getBean(c);
        method.invoke(instance,args);
    }
}
