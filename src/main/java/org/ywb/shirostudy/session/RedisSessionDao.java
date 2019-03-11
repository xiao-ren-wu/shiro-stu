package org.ywb.shirostudy.session;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.UnknownSessionException;
import org.apache.shiro.session.mgt.eis.AbstractSessionDAO;
import org.apache.shiro.util.CollectionUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.ywb.shirostudy.JedisUtil;

import javax.annotation.Resource;
import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/11 23:43
 */

@Component
public class RedisSessionDao extends AbstractSessionDAO {

    @Resource
    private JedisUtil jedisUtil;

    private final String shiro_session_prefix = "tea-session";

    private byte[] getKey(String key){
        return (shiro_session_prefix+key).getBytes();
    }

    private void saveSession(Session session){
        if(session==null||session.getId()==null){
            return;
        }
        byte[] key = this.getKey(session.toString());
        byte[] value = SerializationUtils.serialize(session);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
    }

    @Override
    protected Serializable doCreate(Session session) {
        Serializable sessionId = generateSessionId(session);
        assignSessionId(session,sessionId);
        this.saveSession(session);
        return sessionId;
    }

    @Override
    protected Session doReadSession(Serializable serializable) {
        if(serializable==null){
            return null;
        }
        byte[] key = getKey(serializable.toString());
        byte[] value = jedisUtil.get(key);
        return (Session)SerializationUtils.deserialize(value);
    }

    @Override
    public void update(Session session) throws UnknownSessionException {
        this.saveSession(session);
    }

    @Override
    public void delete(Session session) {
        if (session==null||session.getId()==null){
            return;
        }
        byte[] key = getKey(session.getId().toString());
        jedisUtil.delete(key);
    }

    @Override
    public Collection<Session> getActiveSessions() {
        Set<byte[]> keySet = jedisUtil.keys();
        Set<Session> sessions = new HashSet<>();
        if(CollectionUtils.isEmpty(keySet)){
            return sessions;
        }
        for(byte[] key:keySet){
            Session session = (Session) SerializationUtils.deserialize(jedisUtil.get(key));
            sessions.add(session);
        }
        return sessions;
    }
}
