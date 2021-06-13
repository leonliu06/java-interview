package com.mrliuli;

import javax.xml.stream.events.Characters;
import java.io.IOException;
import java.util.*;


/**
 * @author liu.li
 * @date 2021/5/13
 * @description
 */
public class Result {

    /*
     * Complete the 'countDuplicate' function below.
     *
     * The function is expected to return an INTEGER.
     * The function accepts INTEGER_ARRAY numbers as parameter.
     */

    public static int countDuplicate(List<Integer> numbers) {
        // Write your code here

        if (numbers == null || numbers.size() < 2) {
            return 0;
        }

        Set<Integer> set = new HashSet<>();
        Set<Integer> duplicate = new HashSet<>();
        int count = 0;

        for (Integer number : numbers) {
            if(set.contains(number) && !duplicate.contains(number)) {
                count++;
                duplicate.add(number);
            } else {
                set.add(number);
            }
        }

        return count;

    }

    public static void main(String[] args) {
        System.out.println(countDuplicate(Arrays.asList(1, 3, 1, 4, 5, 6, 3, 2)));
        System.out.println(countDuplicate(Arrays.asList(1, 1, 2, 2, 2, 3)));

        System.out.println(getTime("BZA"));
        System.out.println(getTime("AZGB"));

    }

    public static long getTime(String s) {
        // Write your code here

        HashMap<Character, Integer> curcular = new HashMap<>();
        for(Character c = 'A'; c <= 'Z'; c++) {
            curcular.put(c, c - 'A');
        }

        Character left = 'A';

        int d = 0;

        for(int i = 0; i < s.length(); i++) {

            Character c = s.charAt(i);

            int di = Math.abs(curcular.get(c) - curcular.get(left));

            if(di >= 13) {
                di = 26 - di;
            }

            d += di;

            left = c;

        }

        return d;

    }

    public static List<String> getUsernames(int threshold) {

return null;

    }


}
