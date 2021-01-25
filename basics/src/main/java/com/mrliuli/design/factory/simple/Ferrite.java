package com.mrliuli.design.factory.simple;

/**
 * @author liu.li
 * @date 2021/1/25
 * @description
 */
public class Ferrite implements Metal {

    @Override
    public String name() {
        System.out.println("I am Ferrite");
        return "Ferrite";
    }

}
