package com.mrliuli.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuli on 2019/09/19.
 */
public class CollectionCode {

    /**
     * 1 -为什么HashMap不是线程安全的？
     * 1.1 线程T1和T2同时对一个HashMap进行put操作，如产生hash碰撞，正常情况下，会形成链表，并发情况下，有可能T2线程会覆盖T1线程put的元素。
     * 1.2 线程T1和T2同时对一个HashMap进行resize操作，在jdk1.7中可能出现循环链表，使get一个不存在的元素时，造成死循环，在jdk1.8中不会。
     *  所以，HashMap的线程不安全主要体现在下面两个方面：
     *      1.在JDK1.7中，当并发执行扩容操作时会造成环形链和数据丢失的情况。
     *      2.在JDK1.8中，在并发执行put操作时会发生数据覆盖的情况。
     */

    /**
     * 2 HashSet 与 HashMap 的区别
     * 2.1 HashSet 实现 Set，HashMap 实现 Map
     * 2.2 HashSet 存储 对象 集合，HashMap 存储 Key-Value键-值对 集合
     * 2.3 HashSet 底层 使用 HashMap 实现，.add() 添加一个值时，会作为 HashMap 的 key 来计算 添加
     *
     * 所以，HashSet 存储的值是不重复的，HashMap 存储的 key 是不重复的，但 key 对应的 值可以重复，
     * HashSet 和 HashMap 都可用存储 null
     */

    /**
     * 3 HashMap源码（jdk8）
     * 3.1 put操作时，如哈希碰撞，元素插入链表是从尾部插入（jdk7是从头部插入）
     * 3.2 put操作时，如第一次put，即table为空时，则先调用resize，然后put，非第一次，即table.length > 0时，则先put，再resize。
     * 3.3 resize重新分配元素时，链表上的元素，最多会分为两部分，一部分分配在原来的低位位置，另一部分分配到新扩容的高位位置。
     */


    public static void run(){
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add(null);
        System.out.println(set.toString());
    }

    public static void main(String[] args){
        CollectionCode.run();
    }

}
