package com.mrliuli.design.factory.simple;

/**
 * @author liu.li
 * @date 2021/1/25
 * @description
 */
public class Test {

    private static final String NICKEL = "Nickel";

    private static final String FERRITE = "Ferrite";

    public static void main(String[] args) {
        Metal metal = SimpleMetalFactory.produce(NICKEL);
        metal.name();
        metal = SimpleMetalFactory.produce(FERRITE);
        metal.name();
    }
}
