package com.mrliuli.bean;

/**
 * Created by liuli on 2019/12/25.
 */
public class Cat implements Animal {

    private String name;

    @Override
    public void say() {
        System.out.println("I am " + name + "!");
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
