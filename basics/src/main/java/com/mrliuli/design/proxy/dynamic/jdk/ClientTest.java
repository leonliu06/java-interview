package com.mrliuli.design.proxy.dynamic.jdk;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 *
 */
public class ClientTest {

    public static void main(String[] args)  {

        JDKDynamicProxy jdkDynamicProxy = new JDKDynamicProxy();

        ITargetSubject target = new TargetSubject();

        ITargetSubject proxy = jdkDynamicProxy.getDynamicProxyInstance(target);

        proxy.request();

    }

}
