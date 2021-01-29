package com.mrliuli.design.factory.abstracd;

/**
 * @author liu.li
 * @date 2021/1/26
 * @description
 * 工厂接口
 */
public interface IFactory {

    IProductMetal produceMetal(String name);

    IProductCeramic produceCeramic(String name);

}
