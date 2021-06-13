package com.mrliuli;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

/**
 * @author liu.li
 * @date 2021/5/17
 * @description
 */
public class C extends Thread {

    public static void main(String args[]) throws Exception {
        C b = new C();
        b.run();

        ObjectOutputStream oo = new ObjectOutputStream(new FileOutputStream(new File("D:/Person.txt")));
        oo.writeObject(new Wheel());
        System.out.println("Person对象序列化成功！");
        oo.close();

    }

    public void start() {
        for (int i = 0; i < 10; i++) {
            System.out.println("Value of i = " + i);
        }
    }

}

class Vehicle {}
class Wheel {}
class Car extends Vehicle implements Serializable {}
class Ford extends Car {}
class Dodge extends Car {
    Wheel w = new Wheel();
}
