package com.mrliuli.base;

import java.util.HashMap;

/**
 * Created by liuli on 2019/09/16.
 */
public class Operator {

    /**
     * >> 与 >>> 的区别：
     *
     * >>: 带符号右移，正数右移高位补0，负数右移高位补1。
     * >>>: 无符号右移。无论是正数还是负数，高位通通补0。
     * 对于正数而言，>> 和 >>> 没有区别。
     *
     * 例：
     *   4 >> 1 (00000000 00000000 00000000 00000100 >> 1 -> 00000000 00000000 00000000 00000100) 结果是2；
     *  -4 >> 1 (10000000 00000000 00000000 00000100 >> 1 -> 10000000 00000000 00000000 00000010) 结果是-2；
     */

    /**
     * 异或运算符 ^ 的应用
     * 异或运算符 ^ 的特点：
     *      1. n ^ 0 = n, n ^ n = 0, 即 任何数与 0 异或为其本身，与 本身 异或 会得到 0。
     *      2. 另外，异或运算满足交换规则，即 a ^ b ^ c = a ^ c ^ b
     * 根据此特点，举例下题，一数组如 int[]{1, 2, 3, 2, 3} 只有一个值是唯一的，其他都是成对的，怎样快速找到这个唯一的值，
     * 则 1 ^ 2 ^ 3 ^ 2 ^ 3 = (2 ^ 2) ^ (3 ^ 3) ^ 1 = 0 ^ 0 ^ 1 = 0 ^ 1 = 1, java 实现验证如下
     */

    public static void run(){
        int[] numArr = new int[]{1,2,3,2,3};
        int aim = numArr[0];
        for(int i = 1; i < numArr.length; i++)
        {
            aim = aim ^ numArr[i];
        }
        System.out.println("最后：" + aim);
    }

    public static void hash() {
        String key = "abcdedf";
        int h;
        h = key.hashCode();
        h = h ^ (h >>> 16);
        System.out.println("h = " + h);
    }

    public static void main(String[] args) {
        Operator.run();
        Operator.hash();
    }


}
