package com.mrliuli.design.singleton.threadlocal;

/**
 * Created by liuli on 2019/09/24.
 *
 * 注：
 * 1. 利用 ThreadLocal 类的特点实现 线程内 单例，即在各自的线程内是单例的。
 * 2. ThreadLocal 有个内部类 ThreadLocalMap, 会以 当前线程 Thread.currentThread() 作为 key 来存储 value,
 *      value 通过 set() 方法设置，通过 get() 方法获取 )
 */
public class Singleton {

    private Singleton(){}

    private static final ThreadLocal<Singleton> threadLocal = new ThreadLocal<Singleton>(){
        @Override
        protected Singleton initialValue() {
            return new Singleton();
        }
    };

    public static Singleton getInstance(){
        // 在没有调用 set() 方法时就调用 get()，则 get() 内会调用 initialValue()
        return threadLocal.get();
    }

}
