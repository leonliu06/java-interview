package com.mrliuli.design.proxy.dynamic.jdk;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 */
public interface ITargetSubject {

    void request();

    // 追加一个方法
    void supplementaryMethod();

}
