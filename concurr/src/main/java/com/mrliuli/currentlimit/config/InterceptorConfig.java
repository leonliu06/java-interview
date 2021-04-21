package com.mrliuli.currentlimit.config;

import com.mrliuli.currentlimit.limit.CurrentLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Created by liuli on 2019/10/10.
 */
@Configuration
public class InterceptorConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // 配置 登录拦截器，并配置 InterceptorRegistration 拦截所有请求
        //registry.addInterceptor(new LoginInterceptor()).addPathPatterns("/**");

        registry.addInterceptor(new CurrentLimitInterceptor());

    }

}
