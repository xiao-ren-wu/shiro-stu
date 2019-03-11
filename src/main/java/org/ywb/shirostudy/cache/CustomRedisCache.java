package org.ywb.shirostudy.cache;

import org.apache.shiro.cache.Cache;
import org.apache.shiro.cache.CacheException;
import org.springframework.stereotype.Component;
import org.springframework.util.SerializationUtils;
import org.ywb.shirostudy.JedisUtil;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Set;

/**
 * @author XiaoRenwu
 * e-mail 18629015421@163.com
 * github https://github.com/xiao-ren-wu
 * @version 1
 * @since 2019/3/12 1:02
 */

@Component
public class CustomRedisCache<K,V> implements Cache<K,V> {

    @Resource
    private JedisUtil jedisUtil;

    private final String shiro_cache_prefix = "tea-cache";

    private byte[] getKey(K k){
        if(k==null){
            return null;
        }
        if(k instanceof String){
            return (shiro_cache_prefix+k).getBytes();
        }
        return SerializationUtils.serialize(k);
    }

    @Override
    public V get(K k) throws CacheException {
        byte[] value = jedisUtil.get(getKey(k));
        if(value!=null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public V put(K k, V v) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = SerializationUtils.serialize(v);
        jedisUtil.set(key,value);
        jedisUtil.expire(key,600);
        return v;
    }

    @Override
    public V remove(K k) throws CacheException {
        byte[] key = getKey(k);
        byte[] value = jedisUtil.get(key);
        jedisUtil.delete(key);
        if(value!=null){
            return (V)SerializationUtils.deserialize(value);
        }
        return null;
    }

    @Override
    public void clear() throws CacheException {

    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public Set<K> keys() {
        return null;
    }

    @Override
    public Collection<V> values() {
        return null;
    }
}
