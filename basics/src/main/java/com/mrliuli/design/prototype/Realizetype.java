package com.mrliuli.design.prototype;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 */
public class Realizetype implements Cloneable {

    Realizetype() {
        System.out.println("具体原型创建成功！");
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        System.out.println("具体原型复制成功！");
        return super.clone();
    }
}
