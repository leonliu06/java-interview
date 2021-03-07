package com.mrliuli.design.proxy.dynamic.jdk;

import javax.imageio.ImageTranscoder;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @author leonliu06
 * @date 2021/3/2
 * @description
 *
 * JDK 动态代理类
 *
 *
 *
 */
public class JDKDynamicProxy implements InvocationHandler {

    private ITargetSubject targetSubject;

    // 持有目标对象
    public JDKDynamicProxy(ITargetSubject targetSubject) {
        this.targetSubject = targetSubject;
    }

    // 将目标对象 生成 代理对象
    public ITargetSubject createDynamicProxyInstance() {

        // 获得目标对象的类型信息
        Class targetSubjectClass = targetSubject.getClass();

        // 根据类型对象获取其类加载器
        ClassLoader classLoader = targetSubjectClass.getClassLoader();

        // 根据类型对象获取 对象实现的 所有接口。 targetSubjectClass.getSuperclass() 是获取该类型的 直接父类。
        Class[] interfaces = targetSubjectClass.getInterfaces();

        /**
         * 通过反射动态生成代理类 newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)
         * 参数 loader 使用目标类的类加载器来生成代理
         * 参数 interfaces 目标类实现的所有接口，所有 java 动态代理只能代理 实现了接口的目标类的方法。 代理类会代理目标类所有接口中的方法。
         * 参数 h 只有一个 invoke 方法，代理类方法的调用先发送到 h 中的 invoke 方法
         * 返回 代理类，代理类也实现了 目标类的接口。
         */
        return (ITargetSubject) Proxy.newProxyInstance(classLoader, interfaces, this);

    }


    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        pre();
        Object result = method.invoke(this.targetSubject, args);
        post();

        return result;

    }

    private void pre() {
        System.out.println("执行目标对象前的预处理...");
    }

    private void post() {
        System.out.println("执行目标对象后的后续处理...");
    }


}
