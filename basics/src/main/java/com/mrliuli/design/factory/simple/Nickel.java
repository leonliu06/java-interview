package com.mrliuli.design.factory.simple;

/**
 * @author liu.li
 * @date 2021/1/25
 * @description
 */
public class Nickel implements Metal {



    @Override
    public String name() {
        System.out.println("I am Nickel");
        return "Nickel";
    }

}
