package com.mrliuli.design.proxy.statik;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 */
public class RealSubject implements Subject {

    @Override
    public void request() {
        System.out.println("执行真实主题方法...");
    }

    @Override
    public void anotherRequest() {

        System.out.println("执行真实主题的另一个方法...");

    }
}
