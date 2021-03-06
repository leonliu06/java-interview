package com.mrliuli.design.factory.simple;

/**
 * @author liu.li
 * @date 2021/1/25
 * @description
 * 特点：
 * 1 它是一个<p>具体的类，非接口抽象类</p>。有一个重要的produce()方法，利用if或者switch创建产口并返回。
 * 2 produce()方法通常是静态的，所以也称之为静态工厂。
 * 3 简单工厂模式不在 GoF（四人组，Gang of Four） 23 种设计模式之列。
 * 缺点：
 * 1 扩展性差，我想新增一种金属产品，除了新增一个金属类，还需要修改工厂类方法。
 * 2 不同的产品，需要不同额外参数的时候，如需要金属钴（Chromium）产品的时候不支持。
 */
public class SimpleMetalFactory {

    private SimpleMetalFactory(){}

    public static Metal produce(String name) {
        if("Nickel".equalsIgnoreCase(name)) {
            return new Nickel();
        } else if("Ferrite".equalsIgnoreCase(name)) {
            return new Ferrite();
        }
        return null;
    }
}
