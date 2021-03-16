package com.mrliuli.design.adapter;

/**
 * @author liu.li
 * @date 2021/3/16
 * @description
 *
 * 适配器，转换器
 *
 */
public class Adapter implements ITarget {

    // 关联适配者对象
    private Adaptee adaptee;

    public Adapter(Adaptee adaptee) {
        this.adaptee = adaptee;
    }

    @Override
    public void request() {

        System.out.println("调用适配器方法");

        adaptee.specificRequest();

    }

}
