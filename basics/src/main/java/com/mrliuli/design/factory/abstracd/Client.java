package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/26
 * @description
 *
 * 抽象工厂模式是工厂方法模式的升级版本，工厂方法模式只生产一个等级的产品，而抽象工厂模式可生产多个等级的产品。
 *
 */
public class Client {

    public static void main(String[] args) {

        IFactory factory = new AbstractFactory();

        factory.produceMetal("Nickel");
        factory.produceCeramic("Vase");

    }
}
