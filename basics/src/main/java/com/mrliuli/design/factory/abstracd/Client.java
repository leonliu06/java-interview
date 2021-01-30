package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/26
 * @description
 *
 * 抽象工厂模式是工厂方法模式的升级版本，工厂方法模式只生产一个种类（等级）的产品，而抽象工厂模式可生产多个种类（等级）的产品（一组产品族）。
 *
 */
public class Client {

    // http://c.biancheng.net/view/1351.html

    public static void main(String[] args) {

        IFactory factory = new FactoryShanDong();

        IProductMetal productMetal = factory.produceMetal("");
        productMetal.name();
        IProductCeramic productCeramic = factory.produceCeramic("");
        productCeramic.name();

        factory = new FactoryShangHai();

        productMetal = factory.produceMetal("");
        productMetal.name();
        productCeramic = factory.produceCeramic("");
        productCeramic.name();


    }

}
