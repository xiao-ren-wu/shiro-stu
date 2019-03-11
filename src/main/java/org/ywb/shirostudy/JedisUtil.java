package org.ywb.shirostudy;

import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.Set;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/11 23:45
 */

@Component
public class JedisUtil {

    @Resource
    private JedisPool jedisPool;

    private final String shiro_session_prefix = "tea-session";


    public void set(String key, String value) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(key,value);
        }
    }

    public void expire(byte[] key, int i) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.expire(key,i);
        }
    }

    public String get(String key){
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.get(key);
        }
    }

    public void set(byte[] key, byte[] value) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.set(key,value);
        }
    }

    public byte[] get(byte[] key) {
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.get(key);
        }
    }

    public void delete(byte[] key) {
        try(Jedis jedis = jedisPool.getResource()){
            jedis.del(key);
        }
    }

    public Set<byte[]> keys() {
        try(Jedis jedis = jedisPool.getResource()){
            return jedis.keys((shiro_session_prefix+"*").getBytes());
        }
    }
}
