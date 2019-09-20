package com.mrliuli.collections;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by liuli on 2019/09/19.
 */
public class CollectionCode {

    /**
     * 1 -为什么HashMap不是线程安全的？
     * 1.1
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
