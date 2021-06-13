package com.mrliuli.currentlimit.limit;

import com.mrliuli.currentlimit.cache.MemoryCacheUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author liu.li
 * @date 2021/4/11
 * @description
 */
public class CurrentLimitInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(CurrentLimitInterceptor.class);

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        logger.info("限流拦截 === ");

        if (!(handler instanceof HandlerMethod)) {
            return true;
        }

        Method method = ((HandlerMethod) handler).getMethod();

        if (!method.isAnnotationPresent(CurrentLimit.class)) {
            return true;
        }

        String subject = method.getAnnotation(CurrentLimit.class).subject();

        String key = subject.equalsIgnoreCase("ip") ? IPUtil.getIpAddr(request) : null;

        if (get(key).acquireToken()) {
            logger.info("限流拦截 === 取到令牌");
            return true;
        }

        logger.info("限流拦截 === 未取到令牌");
        throw new RuntimeException("操作频繁");
    }


    private TokenBucket get(String key) {

        TokenBucket tokenBucket = (TokenBucket) MemoryCacheUtil.get(key, 60 * 60 * 1000);

        if (tokenBucket == null) {
            tokenBucket = new TokenBucket(10, 1);
            MemoryCacheUtil.put(key, tokenBucket);
        }

        return tokenBucket;

    }


    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
}
