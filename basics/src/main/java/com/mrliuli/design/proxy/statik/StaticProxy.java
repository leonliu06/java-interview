package com.mrliuli.design.proxy.statik;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 * 静态代理
 */
public class StaticProxy implements Subject {

    private RealSubject realSubject = new RealSubject();

    @Override
    public void request() {

        // 执行实主题之前的预处理
        preRequest();

        // 执行真实主题方法
        realSubject.request();

        // 执行真实主题之后的后续处理
        postRequest();

    }

    @Override
    public void anotherRequest() {
        preRequest();
        realSubject.anotherRequest();
        postRequest();
    }

    private void preRequest() {
        System.out.println("执行真实主题之前的预处理...");
    }

    private void postRequest() {
        System.out.println("执行真实主题之后的后续处理...");
    }
}
