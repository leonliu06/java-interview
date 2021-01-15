package com.mrliuli.design.proxy;

import org.springframework.cglib.proxy.Callback;
import org.springframework.cglib.proxy.Enhancer;

import java.lang.reflect.Proxy;

/**
 * Created by liuli on 2020/06/28.
 */
public class JDKDynamicProxy implements Callback {



    public int main(String[] args) {


//        Proxy.newProxyInstance()

        Enhancer enhancer = new Enhancer();
//        enhancer.setSuperclass();
        enhancer.setCallback(this);

        return 0;
    }
}
