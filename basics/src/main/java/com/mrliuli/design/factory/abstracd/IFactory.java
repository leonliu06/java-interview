package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/26
 * @description
 *
 * 抽象工厂接口，提供了生成产品的方法
 *
 */
public interface IFactory {

    IProductMetal produceMetal(String name);

    IProductCeramic produceCeramic(String name);

}
