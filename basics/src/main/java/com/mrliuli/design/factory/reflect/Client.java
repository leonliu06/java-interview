package com.mrliuli.design.factory.reflect;

import com.mrliuli.design.factory.simple.Metal;

/**
 * @author liu.li
 * @date 2021/2/2
 * @description
 */
public class Client {


    public static void main(String[] args) {

        Metal metal = ReflectFactory.produce("Nickel");
        metal.name();

        metal = ReflectFactory.produce("Ferrite");
        metal.name();
        
    }

}
