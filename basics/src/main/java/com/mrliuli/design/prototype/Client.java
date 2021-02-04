package com.mrliuli.design.prototype;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 */
public class Client {

    public static void main(String[] args) throws CloneNotSupportedException {

        Realizetype obj1 = new Realizetype();

        // 通过原型的一个实例对象来复制生成另一个实例对象
        Realizetype obj2 = (Realizetype) obj1.clone();

        System.out.println("obj1 == obj2 ? " + (obj1 == obj2));

    }


}
