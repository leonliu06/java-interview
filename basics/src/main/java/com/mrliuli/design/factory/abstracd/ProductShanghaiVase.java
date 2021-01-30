package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/30
 * @description
 */
public class ProductShanghaiVase implements IProductCeramic {

    @Override
    public void name() {
        System.out.println("上海工厂的花瓶");
    }

}
