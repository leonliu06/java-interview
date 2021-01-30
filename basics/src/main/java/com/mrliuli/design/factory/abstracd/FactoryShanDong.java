package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/25
 * @description
 *
 * 具体的山东工厂实现了产品的生成方法
 *
 */
public class FactoryShanDong implements IFactory {

    @Override
    public IProductMetal produceMetal(String name) {
        return new ProductJiNanSteel();
    }

    @Override
    public IProductCeramic produceCeramic(String name) {
        return new ProductLinYiVase();
    }

}
