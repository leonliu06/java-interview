package com.mrliuli.design.adapter;

/**
 * @author liu.li
 * @date 2021/3/16
 * @description
 */
public class AdapterClientTest {

    public static void main(String[] args) {

        // 适配器 Adapter 将 适配者 Adaptee 转换为 客户端期望的接口 ITarget
        ITarget target = new Adapter(new Adaptee());

        // 客户端调用
        target.request();

    }
}
