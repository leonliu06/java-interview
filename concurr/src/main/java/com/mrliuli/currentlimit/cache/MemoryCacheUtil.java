package com.mrliuli.currentlimit.cache;

import com.mrliuli.currentlimit.limit.TokenBucket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

/**
 * @author liu.li
 * @date 2021/4/11
 * @description
 */
public class MemoryCacheUtil {

    private static final Logger logger = LoggerFactory.getLogger(MemoryCacheUtil.class);

    // 静态，作为全局缓存
    private static final HashMap<Object, Object> cacheMap = new HashMap<>();

    // 标记缓存的过期时间
    private static final HashMap<Object, Long> keyExpire = new HashMap<>();

    // 默认缓存时间 60 分钟
    private static long expire = 60 * 60 * 1000;


    /**
     * 读缓存，默认不续期
     * @param key
     * @return
     */
    public static Object get(Object key) {

        return get(key, -1);
    }

    /**
     * 读缓存，并且续期
     * @param key
     * @param renewal 读的时候，续期 renewal 毫秒
     * @return
     */
    public static Object get(Object key, long renewal) {

        Object value = cacheMap.get(key);

        if (cacheMap.containsKey(key) && renewal > 0) {
            // 续期
            keyExpire.put(key, System.currentTimeMillis() + renewal);
        }

        return value;

    }

    public static void put(Object key, Object value) {
        put(key, value, expire);
    }

    public static void put(Object key, Object value, long expire) {

        cacheMap.put(key, value);
        keyExpire.put(key, System.currentTimeMillis() + expire);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    Long expire = keyExpire.get(key);
                    if (expire == null) {
                        break;
                    }
                    // 过期，删除缓存
                    if (System.currentTimeMillis() > expire) {
                        MemoryCacheUtil.remove(key);
                        keyExpire.remove(key);
                        logger.info("缓存【{}】已过期，执行删除", key);
                        break;
                    }
                    try {
                        Thread.sleep(30 * 1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

            }
        }).start();
    }

    public static void remove(Object key) {
        keyExpire.remove(key);
        Object value = cacheMap.remove(key);
        if (value instanceof TokenBucket) {
            TokenBucket tokenBucket = (TokenBucket) value;
            tokenBucket.getScheduledExecutorService().shutdown();
        }
    }

}
