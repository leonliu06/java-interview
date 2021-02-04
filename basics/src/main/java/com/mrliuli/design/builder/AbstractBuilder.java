package com.mrliuli.design.builder;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 */
public abstract class AbstractBuilder {

    protected Parlor parlor = new Parlor();

    public abstract void buildWall();

    public abstract void buildSofa();

    public abstract void buildTV();


    // 返回产品对象
    public Parlor getResult() {
        return parlor;
    }


}
