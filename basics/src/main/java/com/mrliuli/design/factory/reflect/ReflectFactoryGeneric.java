package com.mrliuli.design.factory.reflect;

import com.mrliuli.design.factory.simple.Metal;

/**
 * @author liu.li
 * @date 2021/2/2
 * @description
 *
 * 反射+泛型
 */
public class ReflectFactoryGeneric {


    public static <T> T produce(String className) {
         T metal = null;

        try {

            Class clazz = Class.forName(className);

            metal = (T) clazz.newInstance();

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
