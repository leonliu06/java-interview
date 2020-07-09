package com.mrliuli.collections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by liuli on 2019/09/19.
 */
public class CollectionCode {

    /**
     * 1 -为什么HashMap不是线程安全的？
     * 1.1 线程T1和T2同时对一个HashMap进行put操作，如产生hash碰撞，正常情况下，会形成链表，并发情况下，有可能T2线程会覆盖T1线程put的元素。
     * 1.2 线程T1和T2同时对一个HashMap进行resize操作，因jdk1.7中，扩容时，移动元素生成新链表是按头插法进行的，可能出现循环链表，
     * 使得get一个不存在的元素，且该元素索引位置在循环链表位置时，造成对环形链表的死循环遍历，在jdk1.8中不会。
     *  所以，HashMap的线程不安全主要体现如下：
     *      1.在JDK1.7中，当并发执行put操作时，会造成数据丢失，并发扩容操作时会造成死循环的情况。
     *      2.在JDK1.8中，当并发执行put操作时，也会造成数据丢失，但不会形成环形链表，所以不会出现死循环情况。
     */

    /**
     * 2 HashSet 与 HashMap 的区别
     * 2.1 HashSet 实现 Set，HashMap 实现 Map
     * 2.2 HashSet 存储 对象 集合，HashMap 存储 Key-Value键-值对 集合
     * 2.3 HashSet 底层 使用 HashMap 实现，.add() 添加一个值时，会作为 HashMap 的 key 来计算 添加
     *
     * 所以，HashSet 存储的值是不重复的，HashMap 存储的 key 是不重复的，但 key 对应的 值可以重复，
     * HashSet 和 HashMap 中的key都可以存储 null，ConcurrentHashMap 中的 key 和 value 都不可以为 null，否则报空指针异常
     */

    /**
     * 3 HashMap源码（jdk7）
     * 3.1 `HashMap`类主要由一个`Entry`数组`Entry<K,V>[] table`构成;
     */

    /**
     * 4 HashMap源码（jdk8）
     * 4.1 HashMap`类主要由一个`Node`数组`Node<K,V>[] table`构成;
     * 4.2 put操作时，如哈希碰撞，元素插入链表是从尾部插入（jdk7是从头部插入）
     * 4.3 put操作时，如第一次put，即table为空时，则先调用resize，然后put，非第一次，即table.length > 0时，则先put，再resize。
     * 4.4 resize重新分配元素时，链表上的元素，最多会分为两部分，一部分分配在原来的低位位置，另一部分分配到新扩容的高位位置。
     */


    /**
     * 5 ConcurrentHashMap源码（jdk1.7）
     * 5.1 ConcurrentHashMap类主要由一个Segment数组（Segment<K,V>[] segments）构成；
     * 5.2 Segment的数量size为并发级别concurrencyLevel的大小，默认为 DEFAULT_CONCURRENCY_LEVEL = 16；
     * 5.3 每个Segment表的容量为ConcurrentHashMap初始容量initialCapacity（默认为DEFAULT_INITIAL_CAPACITY = 16）
     *      除以Segment的数量ssize，最小容量为2（MIN_SEGMENT_TABLE_CAPACITY = 2）；
     * 5.4 Segment是一个ReentrantLock类，含有一个HashEntry<K,V>[]数组（HashEntry<K,V>[] table）;
     *
     */

    /**
     * 6 ConcurrentHashMap源码（jdk1.8）
     * 6.1 `ConcurrentHashMap`由一个`Node`数组`table`构成
     * 6.2 `table`元素`Node`是一个链表结点，在`table`槽位上的链表元素大于等于`8`，并且`Node`数组`table`的长度大于`64`时，会转化为树形链表，扩容时，树元素个数小于等于6时，会转化为链表
     * 6.3 `ConcurrentHashMap`通过`transfer`方法扩容，扩容时，先从高位索引遍历数组`table`，然后再遍历索引处的链表或树，将链表（或树）上结点分成两个链表，一个保留在原来位置，一个向后移动`n`位
     * 6.4 `ConcurrentHashMap`利用`synchronized`和`Unsafe`类的`CAS`方法来控制并发
     */


    public static void run(){
        Set<String> set = new HashSet<>();
        set.add("a");
        set.add(null);
        System.out.println(set.toString());
    }

    public static void main(String[] args){
        CollectionCode.run();
        ConcurrentHashMap<String, String> concurrentHashMap = new ConcurrentHashMap<>();
        HashMap<String, String> hashMap = new HashMap<>();
        for(int i = 0; i < 50; i++){
            concurrentHashMap.put(String.valueOf(i), "a");
        }
        concurrentHashMap.put("test", "aaaa");
        System.out.println("xx");
    }

}
