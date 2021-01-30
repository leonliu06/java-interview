package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/30
 * @description
 *
 * 具体的上海工厂
 *
 */
public class FactoryShangHai implements IFactory {

    @Override
    public IProductMetal produceMetal(String name) {
        return new ProductShanghaiAlloy();
    }

    @Override
    public IProductCeramic produceCeramic(String name) {
        return new ProductShanghaiVase();
    }

}
