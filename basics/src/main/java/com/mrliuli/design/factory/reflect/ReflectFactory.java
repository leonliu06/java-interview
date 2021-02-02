package com.mrliuli.design.factory.reflect;

import com.mrliuli.design.factory.simple.Metal;

/**
 * @author liu.li
 * @date 2021/2/2
 * @description
 *
 * 利用java反射实现工厂模式
 *
 * 对于简单工厂（静态工厂），每增加一个产品类，都要修改工厂类，可以利用 java 反射来解决这个问题
 *
 */
public class ReflectFactory {

    public static Metal produce(String className) {

        Metal metal = null;

        try {

            Class clazz = Class.forName("com.mrliuli.design.factory.simple" + "." + className);

            metal = (Metal) clazz.newInstance();

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

        return metal;

    }



}
