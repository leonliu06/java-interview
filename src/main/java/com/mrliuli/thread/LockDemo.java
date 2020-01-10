package com.mrliuli.thread;

import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuli on 2020/01/06.
 */
public class LockDemo {
    /**
     * https://blog.csdn.net/zhwang102107/article/details/83030604
     *
     * ReentrantLock
     * 1. ReentrantLock，即可重入锁（Java环境下，synchronized也是可重入锁），是唯一实现了Lock接口的类。
     *  可重入指：当一个线程请求得到一个对象锁后再次请求此对象锁，可以再次得到该对象锁。
     */

    public static void main(String[] args){

        ReentrantLock reentrantLock = new ReentrantLock();

        reentrantLock.lock();
        System.out.println("reentrantLock.isLocked() = " + reentrantLock.isLocked());
        System.out.println("线程：" + Thread.currentThread().getId() + "得到锁");

        // 再次请求锁
        reentrantLock.lock();
        System.out.println("reentrantLock.isLocked() = " + reentrantLock.isLocked());
        System.out.println("线程：" + Thread.currentThread().getId() + "再次得到锁");

        // 得到锁两次，则也需要释放锁两次
        reentrantLock.unlock();
        System.out.println("reentrantLock.isLocked() = " + reentrantLock.isLocked());
        reentrantLock.unlock();
        System.out.println("reentrantLock.isLocked() = " + reentrantLock.isLocked());


    }
}
