package com.mrliuli.design.proxy.dynamic.jdk;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 *
 */
public class ClientTest {

    public static void main(String[] args)  {

        ITargetSubject target = new TargetSubject();

        JDKDynamicProxy jdkDynamicProxy = new JDKDynamicProxy(target);

        ITargetSubject proxy = jdkDynamicProxy.createDynamicProxyInstance();

        System.out.println(String.format("生成代理类：%s", proxy.getClass().getName()));

        proxy.request();

        proxy.supplementaryMethod();


    }

}
