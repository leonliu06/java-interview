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
     * ConcurrentHashMap(jdk1.7)中用到的几个Unsafe类的方法：
     * 1. getObject
     *      // Fetches a reference value from a given Java variable.
     *      public native Object getObject(Object o, long offset);
     *   获取给定Java变量o中偏移地址为offset的一个引用值（属性的值）。此方法可突破修饰符的限制。类似的方法有getInt、getDouble等。
     * 2. getObjectVolatile
     *      // Fetches a reference value from a given Java variable, with volatile load semantics.
     *      // Otherwise identical to {@link #getObject(Object, long)}
     *      public native Object getObjectVolatile(Object o, long offset);
     *   同上述getObject方法类似，但附加了'volatile'加载语义，即强制从主存中获取属性值。此方法要求要获取的属性被volatile修饰，
     *   否则将和getObject方法相同。类似的方法还有getIntVolatile、getDoubleVolatile等。
     *
     * 3. compareAndSwapObject
     *      // Atomically update Java variable to <tt>x</tt> if it is currently holding <tt>expected</tt>.
     *      // @return <tt>true</tt> if successful
     *      public final native boolean compareAndSwapObject(Object o, long offset, Object expected, Object x);
     *   如果变量o中偏移地址为offset的属性值为expected的话，则更新该属性值为x，即CAS操作，该过程为原子性操作。更新成功返回true，否则false。
     *   类似方法有compareAndSwapInt和compareAndSwapLong。
     *
     */

    public static void main(String[] args){
        try {
            Unsafe unsafe = sun.misc.Unsafe.getUnsafe();
            System.out.println(unsafe.addressSize());
        } catch (SecurityException e){
            System.out.println(e.getMessage());
        }
    }

}
