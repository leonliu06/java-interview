package com.mrliuli.algorithm;

import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.Stack;

/**
 * Created by liuli on 2020/01/03.
 */
public class StackDemo {

    private static final HashMap<Character, Character> match = new HashMap<>();

    static {
        match.put('(', ')');
        match.put('[', ']');
        match.put('{', '}');
    }

    public static void main(String[] args){
        String brackets = "{[(4)]}";
        System.out.println(isBracketPair(brackets));
    }

    /**
     * 括号是否成对出现（匹配）
     * @param brackets
     * @return
     */
    public static boolean isBracketPair(String brackets) {

        Stack<Character> stack = new Stack<>();
        if(!StringUtils.isEmpty(brackets)){
            for(int i = 0; i < brackets.length(); i++){
                char c = brackets.charAt(i);
                // 左括号入栈
                if(match.containsKey(c)){
                    stack.push(c);
                    // 右括号
                } else if(match.containsValue(c)){
                    if(stack.isEmpty()){
                        return false;
                    }
                    // 左右括号匹配
                    if(match.get(stack.peek()) == c){
                        stack.pop();
                    } else {
                        return false;
                    }
                }

            }
        }

        return stack.isEmpty();

    }


}
