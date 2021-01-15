package com.mrliuli.design.singleton.dcl;

/**
 * Created by liuli on 2019/09/19.
 *
 * 懒汉模式，双重加锁检查DCL（Double Check Lock）
 * 优点：懒加载，线程安全
 * 注：实例必须有volatile关键字修饰，保证初始化安全
 */
public class Singleton {

    // volatile 修饰
    private static volatile Singleton instance = null;

    // 私有构造函数
    private Singleton(){}

    public static Singleton getInstance(){
        // 先检查
        if(instance == null){
            // 同步块
            synchronized (Singleton.class){
                // 再次检查
                if(instance == null){
                    instance = new Singleton();
                }
            }
        }
        return instance;
    }
}
