package com.mrliuli.algorithm;

import java.util.Arrays;

/**
 * Created by liuli on 2019/10/28.
 */
public class Sort {

    public static void main(String[] args){
        bubbleSort();
    }

    /**
     * 冒泡排序
     */
    public static void bubbleSort(){

        int[] a = {3, 1, 8, 5, 6, 3, 9};

        // 外层：遍历 a.length-1 次
        for(int i = 0; i < a.length - 1; i++){
            // 内层：两两比较 a.length-i-1 次
            for(int j = 0; j < a.length - i - 1; j++){
                if(a[j] > a[j+1]){
                    int temp = a[j];
                    a[j] = a[j+1];
                    a[j+1] = temp;
                }
            }
        }

        System.out.println(Arrays.toString(a));

    }

}
