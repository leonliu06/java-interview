package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/26
 * @description
 */
public class Client {

    public static void main(String[] args) {

        IFactory factory = new AbstractFactory();

        factory.produceMetal("Nickel");
        factory.produceCeramic("Vase");

    }
}
