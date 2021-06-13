package com.mrliuli;

/**
 * @author liu.li
 * @date 2021/5/22
 * @description
 */
public class Test {

    public static void main(String[] args) {
        foo(4);
    }

    public static void foo(int num) {
        /**
         *   1
         *   22
         *  333
         *  4444
         * 55555
         */

        for(int i = 1; i <= num; i++) {
            String temp = String.valueOf(i);
            for(int j = 1; j < i; j++) {
                temp = temp + temp;
            }
            System.out.println(temp);
        }


    }


}
