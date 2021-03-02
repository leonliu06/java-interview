package com.mrliuli.design.proxy.dynamic.jdk;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 */
public class TargetSubject implements ITargetSubject {

    @Override
    public void request() {
        System.out.println("执行目标对象的方法...");
    }

}
