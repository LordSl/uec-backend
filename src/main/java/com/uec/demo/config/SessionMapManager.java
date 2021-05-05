package com.uec.demo.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.websocket.Session;
import java.util.HashMap;

@Configuration
public class SessionMapManager {
    HashMap<String, HashMap<String, Session>> rooms;

    @Bean
    public HashMap<String, HashMap<String, Session>> getRooms(){
        rooms = new HashMap<>();
        return rooms;
    }

}
