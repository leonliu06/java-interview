## 1 静态代理特点：
- 需要编写代理类
- 目标类添加新的方法时，代理类如要代理新方法，则也要重新修改

## 2 JDK动态代理特点：
- 通过方法 `Proxy.newProxyInstance(ClassLoader loader,Class<?>[] interfaces,InvocationHandler h)` 利用反射原理在运行时动态生成代理类
- 目标类添加新的方法时，代理类不需要修改，即可自动代理执行目标类的所有方法
- 由方法 `Proxy.newProxyInstance` 的参数可知，它只能对 实现了接口的目标类生成代理类，并且生成的代理类也是实现相同的接口

## 3 CGLIB动态代理特点：
- 首先需要引入CGLIB包 `compile 'cglib:cglib:3.3.0'`
- 通过 `Enhancer` 来生成代理类，由 `Enhancer#setSuperclass()` 可知，代理类继承目标类，所以`CGLIB`不能代理
`final`类，目标类的 `final` 方法也不能代理
- `Enhancer` 有 `enhancer.setInterfaces(Target.class.getInterfaces())` 方法，可知 `CGLIB` 也能代理实现了接口的目标类


