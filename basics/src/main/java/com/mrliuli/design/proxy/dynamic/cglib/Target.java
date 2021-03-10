package com.mrliuli.design.proxy.dynamic.cglib;

/**
 * @author liu.li
 * @date 2021/3/10
 * @description
 */
public class Target {

    public Boolean request() {
        System.out.println("执行目标类的方法...");
        return Boolean.TRUE;
    }
}
