package com.mrliuli.design.proxy.dynamic.jdk;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 *
 * JDK 动态代理
 *
 * 
 *
 */
public class JDKDynamicProxy implements InvocationHandler {

    private ITargetSubject targetSubject;

    public ITargetSubject getDynamicProxyInstance(ITargetSubject targetSubject) {

        this.targetSubject = targetSubject;
        Class<?> clazz = targetSubject.getClass();
        return (ITargetSubject) Proxy.newProxyInstance(clazz.getClassLoader(), clazz.getInterfaces(), this);

    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        pre();
        Object result = method.invoke(this.targetSubject, args);
        post();

        return result;

    }

    private void pre() {
        System.out.println("执行目标对象前的预处理...");
    }

    private void post() {
        System.out.println("执行目标对象后的后续处理...");
    }


}
