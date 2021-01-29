package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/25
 * @description
 *
 * 抽象工厂模式
 *
 */
public class AbstractFactory implements IFactory {

    @Override
    public IProductMetal produceMetal(String name) {
        return null;
    }

    @Override
    public IProductCeramic produceCeramic(String name) {
        return null;
    }

}
