package com.uec.demo.util;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.params.SetParams;

@Component
public class GlobalJedis {
    Jedis jedis;

    public GlobalJedis(){
        jedis =  new Jedis("localhost",6379);
    }

    public boolean setnx(String key, String value, long time){
        SetParams params = new SetParams();
        params.ex(time);
        params.nx();
        String ex = jedis.set(key,value,params);
        return !(ex==null);
    }

    public boolean del(String key){
        return jedis.del(key)==1L;
    }
}
