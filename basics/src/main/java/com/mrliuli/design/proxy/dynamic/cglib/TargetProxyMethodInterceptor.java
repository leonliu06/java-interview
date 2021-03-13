package com.mrliuli.design.proxy.dynamic.cglib;

import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author leonliu06
 * @date 2021/3/14
 * @description
 */
public class TargetProxyMethodInterceptor implements MethodInterceptor {

    // 代理类的所有方法调用执行此方法，而非原来的目标类的方法，目标类的方法通过该方法中参数 proxy 来调用
    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {

        System.out.println("before...");

        // 通过 参数 MethodProxy proxy 来调用原来的目标类方法
        Object object = proxy.invokeSuper(obj, args);

        System.out.println("after...");

        return object;

    }

}
