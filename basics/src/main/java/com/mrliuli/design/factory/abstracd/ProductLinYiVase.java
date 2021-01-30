package com.mrliuli.design.factory.abstracd;

import com.mrliuli.design.factory.method.IProduct;

/**
 * @author liu.li
 * @date 2021/1/30
 * @description
 */
public class ProductLinYiVase implements IProductCeramic {

    @Override
    public void name() {
        System.out.println("山东工厂的花瓶");
    }
}
