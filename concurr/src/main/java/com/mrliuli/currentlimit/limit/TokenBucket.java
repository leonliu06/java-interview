package com.mrliuli.currentlimit.limit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author liu.li
 * @date 2021/4/11
 * @description
 *
 * 令牌桶算法限流
 *
 */
public class TokenBucket {

    private static final Logger logger = LoggerFactory.getLogger(TokenBucket.class);

    // 令牌桶的容量
    private int capacity;

    // 生成令牌的速率 每秒
    private int rate;

    // 当前令牌数量
    private int tokens;

    // 上一次取令牌的时间
    private long lastTime = System.currentTimeMillis();

    private int bucketId;

    private ScheduledExecutorService scheduledExecutorService;

    public TokenBucket() {
        this(100, 10);
    }

    public TokenBucket(int capacity, int rate) {

        this.capacity = capacity;
        this.rate = rate;
        this.tokens = capacity;

        bucketId = this.hashCode();

        ScheduledExecutorService scheduledThreadPool = Executors.newSingleThreadScheduledExecutor();
        // 周期执行
        scheduledThreadPool.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {

                long now = System.currentTimeMillis();
                // 此次生成令牌的数量
                int add = (int) ((now - lastTime) * rate / 1000);
                // 当前令牌数
                tokens = Math.min(capacity, tokens + add);

                logger.info("桶【{}】生成令牌，当前令牌数：【{}】", bucketId, tokens);

            }
        }, 0,1000, TimeUnit.MILLISECONDS);

        this.scheduledExecutorService = scheduledThreadPool;
    }

    /**
     * 取令牌
     * @return
     */
    public boolean acquireToken() {

        // 标记最后一次取令牌的时间
        lastTime = System.currentTimeMillis();


        if (tokens > 0) {
            tokens--;
            logger.info("桶【{}】，取到令牌，剩余令牌数：【{}】", bucketId, tokens);
            return true;
        }

        logger.info("桶【{}】，未取到令牌，剩余令牌数：【{}】", bucketId, tokens);

        return false;

    }

    public ScheduledExecutorService getScheduledExecutorService() {
        return scheduledExecutorService;
    }

}
