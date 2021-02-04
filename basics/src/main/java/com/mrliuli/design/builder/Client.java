package com.mrliuli.design.builder;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 */
public class Client {

    public static void main(String[] args) {

        Director director = new Director(new ParlorBuilder());

        Parlor parlor = director.buildParlor();

        parlor.show();


    }
}
