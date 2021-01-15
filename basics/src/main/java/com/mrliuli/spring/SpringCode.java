package com.mrliuli.spring;

import com.mrliuli.bean.Animal;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import javax.annotation.Resource;

/**
 * Created by liuli on 2019/09/20.
 */
public class SpringCode {

    /**
     * - @Autowired 与 @Resource 的区别：
     *
     * 1 @Autowired
     * 1.1 按类型 byType 注入bean，属于 Spring 的注解，当IOC容器没有这个类型的bean或存在这个类型的多个bean（多例）时，都会抛出 BeanCreationException 异常。
     * 1.2 如要允许注解的字段为 null，可以设置它的属性 required = false
     * 1.3 如果存在多例的bean, 可以配合 @Qualifier 注解来设置具体 bean 的名称，以确定单例的 bean
     * 1.4 默认别名就是当前类名，但是首字母小写
     *
     * 2 @Resource
     * 2.1 按名称 byName （变量名） 注入bean，属于 J2EE 的注解
     * 2.2 @Resource 有两个属性，name 和 type，默认按 name 来匹配 bean
     * 2.3 如未设置 name 和 type，对于字段 ，默认按字段名称来匹配，对于setter方法，默认按属性名称来匹配，
     *      如果没有匹配，则回退为一个原始类型进行匹配，如果匹配则自动装配
     * 2.2 如设置了 name，则从IOC容器中查找名称（id）为 name 的 bean 进行注入，找不到，则抛出异常
     * 2.3 如设置了 type, 则从IOC窗口中查找类型为 type 的 bean 进行注入，找不到或找到多例，则抛出异常
     * 2.4 如同时设置了 name 和 type，则从IOC容器中找到唯一匹配的 bean 进行注入，找不到，则抛出异常
     *
     * 注：@Resource 的 byName 是指变量名，即根据变量名去查询 同名称的 bean
     *    -@Service 注解声明的 bean 的名称默认是 类名的小写，可设置@Service 的 name 属性来自定义 bean 的名称
     */

    //@Resource

    /**
     * Filter 与 Interceptor 区别
     *
     *
     * Filter vs Interceptor
     * https://stackoverflow.com/questions/8000844/spring-handlerinterceptor-vs-servlet-filters
     */

    /**
     * Spring IOC是如何解决循环依赖的？
     * 1. 什么是循环依赖？
     *  1.1 属性循环依赖，如：public class A { private B b; } public class B { private A a; }
     *  1.2 构造器循环依赖
     * 2. 解决原理：
     *  IOC只能够解决属性之间的循环依赖，不能解决构造器之间的循环依赖，如有Bean构造器之间互相依赖，则报错。
     *  SpringIOC解决属性循环依赖：
     *  IOC容器里bean的初始化的过程分为三个步骤：创建实例、属性注入实例、回调实例实现的接口方法。
     *  IOC解决循环依赖就是在创建实例和属性注入这两个步骤之间，IOC在创建实例尚未完成属性注入时，
     *  会在早期将A的引用缓存到earlySingletonObjects里，即早期暴露引用，当IOC发现A依赖B，B又依赖A时，
     *  B对象创建时会直接从缓存里引用A对象，以完成B对象的创建。
     */

    /**
     *
     * @param args
     */
    public static void main(String[] args) {

        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");

        Animal animal = (Animal) applicationContext.getBean("animal");
        animal.say();

    }

}
