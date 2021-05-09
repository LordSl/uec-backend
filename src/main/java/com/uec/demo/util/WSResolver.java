package com.uec.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;

@Component
public class WSResolver {
    HashMap<String, HashMap<String, Method>> routeMap;
    @Autowired
    BeanFetcher beanFetcher;
    String configJsonFilePath = "src/main/resources/WSResolveMap.json";
    //todo 目前还是手工注入，之后会改成json或xml注入

    public WSResolver() throws NoSuchMethodException, ClassNotFoundException {
        routeMap = new HashMap<>();
        String jsonStr = GlobalTrans.getJsonString(configJsonFilePath);
        JSONObject jo = JSON.parseObject(jsonStr);
        String pkg = jo.getString("pkg");
        JSONArray clzs = jo.getJSONArray("classes");
        for (Object o1 : clzs) {
            JSONObject clz = (JSONObject) o1;
            String clzName = clz.getString("class");
            HashMap<String, Method> map = new HashMap<>();
            routeMap.put(clzName, map);
            JSONArray mapping = clz.getJSONArray("mapping");
            for (Object o2 : mapping) {
                JSONObject kv = (JSONObject) o2;
                String type = kv.getString("type");
                String method = kv.getString("method");
                Method m = getMethodByName(Class.forName(pkg + "." + clzName), method);
                map.put(type, m);
            }
        }
    }

    public void proxy(String className, String methodName, Object... args) throws InvocationTargetException, IllegalAccessException {
        HashMap<String, Method> map = routeMap.get(className);
        Method method = map.get(methodName);
        Class<?> c = method.getDeclaringClass();
        Object instance;
        if (c.isInterface()) instance = beanFetcher.getBeanOfType(c);
        else instance = beanFetcher.getBean(c);
        method.invoke(instance, args);
    }

    private Method getMethodByName(Class<?> c, String name) {
        Method[] methods = c.getDeclaredMethods();
        for (Method m : methods) {
            if (m.getName().equals(name)) return m;
        }
        return null;
    }
}
