package com.mrliuli.design.proxy.dynamic.cglib;

import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.lang.reflect.Method;

/**
 * @author liu.li
 * @date 2021/3/10
 * @description
 */
public class CglibProxyClientTest {


    public static void main(String[] args) {

        Target target = new Target();

        Enhancer enhancer = new Enhancer();

        // 继承目标类
        enhancer.setSuperclass(target.getClass());
        // 也可以使用接口
//        enhancer.setInterfaces(Target.class.getInterfaces());
        // 设置回调，这里可以设置很多回调类型，有FixedValue,InvocationHandler,MethodInterceptor等
        // InvocationHandler: 增加方法，添加额外的功能
        // MethodInterceptor: 与 InvocationHandler 功能类似，但是控制的权限更多
        // InvocationHandler 有些方法调用会引起无限循环，一般使用 MethodHandler
        enhancer.setCallback(new MethodInterceptor() {
            @Override
            public Object intercept(Object obj, Method method, Object[] args, MethodProxy proxy) throws Throwable {
                System.out.println("before...");
                Object object = proxy.invokeSuper(obj, args);
                System.out.println("after...");
                return object;
            }
        });

        // 生成代理类
        Object object = enhancer.create();

        System.out.println(String.format("生成的代理类类型：【%s】", object.getClass().getName()));

        Target proxy = (Target) object;

        Boolean ret = proxy.request();

        System.out.println("目标类返回：" + ret);

        System.out.println(object.getClass().getName());

    }

}
