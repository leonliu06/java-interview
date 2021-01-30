package com.mrliuli.design.factory.method;

/**
 * @author liu.li
 * @date 2021/1/29
 * @description
 * 具体的镍工厂
 */
public class FactoryNickel implements IFactory {

    @Override
    public IProduct produceProduct() {
        System.out.println("produce nickel");
        return new ProductNickel();
    }

}
