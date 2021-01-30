package com.mrliuli.design.factory.method;

/**
 * @author liu.li
 * @date 2021/1/30
 * @description
 * 具体产品：镍金属
 */
public class ProductNickel implements IProduct {

    @Override
    public void name() {
        System.out.println("Nickel");
    }

}
