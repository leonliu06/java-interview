package com.mrliuli.design.singleton.holder;

/**
 * Created by liuli on 2019/09/19.
 *
 * 优点：将懒加载与线程安全完美结合的一种实现方式（无锁，由JVM保证线程安全）
 */
public class Singleton {

    /**
     * 私有化构造方法
      */
    private Singleton(){}

    /**
     * 静态成员式内部类，该内部类与外部类的实例没有绑定关系，且只有被调用到都会装载，从而实现延迟加载
     */
    private static class SingletonHolder{
        // 静态，由JVM来保证线程安全
        private static Singleton instance = new Singleton();
    }

    public static Singleton getInstance(){
        return SingletonHolder.instance;
    }

}
