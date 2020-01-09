package com.mrliuli.base;

import sun.misc.Unsafe;

/**
 * Created by liuli on 2020/01/09.
 */
public class UnsafeDemo {

    /**
     * Unsafe类是直接通过内存地址来修改字段的类，Unsafe使Java拥有了像C语言的指针一样操作内存空间的能力。
     *
     * Unsafe的初始化方法是一个单例模式。限制了它的 ClassLoader，如果这个方法的调用实例不是由Boot ClassLoader加载的，则会报错。
     *
     * 主要功能：
     * 1. 操纵对象属性
     *      public native long objectFieldOffset(Field f);
     *    通过此方法可以获取对象中某个属性的内存偏移地址，然后可根据偏移地址直接对属性进行修改，属性是否可读都无所谓，都能修改。
     *      Field name = user.getClass().getDeclaredField("name");
     *      long nameOffset = unsafe.objectFieldOffset(name);
     *      unsafe.putObject(user, nameOffset, "jim");
     *
     * 2. 操纵数组元素
     *      public native int arrayBaseOffset(Class arrayClass); // 获取数组第一个元素的偏移地址
     *      public native int arrayIndexScale(Class arrayClass); // 获取数组中元素的增量地址
     *    arrayBaseOffset与arrayIndexScale配合起来使用，就可以定位数组中每个元素在内存中的位置。
     *    索引为 i 的元素可以使用如下代码定位：
     *      int baseOffset = unsafe.arrayBaseOffset(array.getClass());
     *      int indexScale = unsafe.arrayIndexScale(array.getClass());
     *      baseOffset + i*indexScale
     *
     * 3. 线程挂起与恢复、CAS
     *      public native void park(boolean var1, long var2); // 调用 park后，线程将一直阻塞直到超时或者中断等条件出现
     *      public native void unpark(Object var1); // unpark可以终止一个挂起的线程，使其恢复正常
     *    整个并发框架中对线程的挂起操作被封装在 LockSupport类中，LockSupport类中有各种版本park方法，但最终都调用了Unsafe.park()方法。
     *
     *
     */

    public static void main(String[] args){
        try {
            Unsafe unsafe = Unsafe.getUnsafe();
            System.out.println(unsafe.addressSize());
        } catch (SecurityException e){
            System.out.println(e.getMessage());
        }
    }

}
