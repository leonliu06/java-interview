package com.mrliuli.design.factory.abstracd;

import com.mrliuli.design.factory.method.IProduct;

/**
 * @author liu.li
 * @date 2021/1/30
 * @description
 */
public class ProductShanghaiAlloy implements IProductMetal {

    @Override
    public void name() {
        System.out.println("上海工厂的合金");
    }

}
