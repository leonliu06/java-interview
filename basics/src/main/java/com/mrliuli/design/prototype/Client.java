package com.mrliuli.design.prototype;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 */
public class Client {

    public static void main(String[] args) throws CloneNotSupportedException {

        Realizetype obj1 = new Realizetype();
        Realizetype obj2 = (Realizetype) obj1.clone();

        System.out.println("obj1 == obj2 ? " + (obj1 == obj2));

    }


}
