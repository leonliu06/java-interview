package com.mrliuli.design.proxy.statik;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 * 静态代理
 */
public class StaticProxyClientTest {

    public static void main(String[] args) {

        Subject subject = new StaticProxy();

        subject.request();

        subject.anotherRequest();

    }

}
