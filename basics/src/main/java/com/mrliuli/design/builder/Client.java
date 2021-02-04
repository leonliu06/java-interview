package com.mrliuli.design.builder;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 *
 * 构建者模式（Builder），用于对一个复杂对象的构建，构建这个对象通常需要很多步骤，强调的是一步步创建。
 *
 * 如果一个产品只有一个构造过程，那么生成器模式就退化成了简单工厂械。
 *
 */
public class Client {

    public static void main(String[] args) {

        Director director = new Director(new ParlorBuilder());

        Parlor parlor = director.buildParlor();

        parlor.show();


    }
}
