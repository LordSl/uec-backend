package com.uec.demo.util;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import redis.clients.jedis.Jedis;

@Configuration
public class JedisConfig {
    Jedis jedis;

    public JedisConfig(){
        jedis =  new Jedis("localhost",6379);
    }

    @Bean
    public Jedis get(){
        return jedis;
    }
}
