package com.mrliuli.design.builder;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 *
 * 指挥者
 *
 */
public class Director {

    private AbstractBuilder builder;

    public Director(AbstractBuilder builder) {
        this.builder = builder;
    }

    public Parlor buildParlor() {

        builder.buildWall();
        builder.buildSofa();
        builder.buildTV();

        return builder.getResult();

    }

}
