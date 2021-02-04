package com.mrliuli.design.builder;

/**
 * @author liu.li
 * @date 2021/2/4
 * @description
 *
 * 客厅构建者
 *
 */
public class ParlorBuilder extends AbstractBuilder {

    @Override
    public void buildWall() {
        parlor.setWall("wall");
        System.out.println("构建樯");
    }

    @Override
    public void buildSofa() {
        parlor.setSofa("sofa");
        System.out.println("构建沙发");
    }

    @Override
    public void buildTV() {
        parlor.setTv("TV");
        System.out.println("构建TV");
    }

    public Parlor getParlor() {
        return parlor;
    }

}
