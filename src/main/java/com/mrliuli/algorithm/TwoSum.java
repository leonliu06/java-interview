package com.mrliuli.algorithm;

import java.util.Arrays;
import java.util.HashMap;

/**
 * Created by liuli on 2020/07/08.
 */
public class TwoSum {

    public static void main(String[] args) {

        int[] result = twoSum(new int[]{2, 7, 11, 15}, 9);
        System.out.println(Arrays.toString(result));

    }

    public static int[] twoSum(int[] nums, int target) {

        HashMap temp = new HashMap<Integer, Integer>();

        for(int i = 0; i < nums.length; i++) {
            int complement = target - nums[i];
            if(temp.containsKey(complement)) {
                return new int[]{(int)(temp.get(complement)), i};
            } else {
                temp.put(nums[i], i);
            }
        }

        throw new IllegalArgumentException("不存在");
    }

}
