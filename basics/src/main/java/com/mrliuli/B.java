package com.mrliuli;

/**
 * @author liu.li
 * @date 2021/5/17
 * @description
 */
class B extends A {

    public String returnSomeValue() throws Exception {
        return "someValueB";
    }

    public static void main(String[] args) throws Exception {
        B b = new B();
        System.out.println(b.returnSomeValue());
    }


}
