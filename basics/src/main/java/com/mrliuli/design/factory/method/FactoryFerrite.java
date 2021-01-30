package com.mrliuli.design.factory.method;

/**
 * @author liu.li
 * @date 2021/1/29
 * @description
 * 具体的纯铁工厂
 */
public class FactoryFerrite implements IFactory {

    @Override
    public IProduct produceProduct() {
        System.out.println("produce ferrite");
        return new ProductFerrite();
    }

}
