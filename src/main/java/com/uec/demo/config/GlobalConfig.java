package com.uec.demo.config;

import com.uec.demo.util.GlobalJedis;
import com.uec.demo.util.GlobalLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GlobalConfig {
    @Autowired
    GlobalJedis jedis;
    @Autowired
    GlobalLogger logger;

    @Bean
    public void init(){
        logger.log("开始");
    }
}
