package org.ywb.shirostudy.config;

import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.ywb.shirostudy.session.RedisSessionDao;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/11 23:54
 */

@Configuration
public class Config {


    @Bean
    public JedisPool getJedisPool(@Value("host")String host,
                                  @Value("port")Integer port){
        return new JedisPool(host,port);
    }



}
