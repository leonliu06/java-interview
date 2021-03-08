&emsp;&emsp;JDK动态代理类是通过静态方法 `Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)` 基于java的反射原理动态生成的。
方法的三个参数和返回值分别是：
- 参数 loader 使用目标类的类加载器来生成代理
- 参数 interfaces 目标类实现的所有接口，所有 java 动态代理只能代理 实现了接口的目标类的方法。 代理类会代理目标类所有接口中的方法。
- 参数 h 只有一个 invoke 方法，代理类方法的调用先发送到 h 中的 invoke 方法
- 返回 代理类，代理类也实现了 目标类的接口。

&emsp;&emsp;由其参数和返回值可知JDK生成动态代理类机制的特点为：
- 动态代理类是对目标类的接口中的方法进行代理，所以目标类必须实现接口
- 动态代理类也是实现了相同的接口，所以代理类只能执行目标类中接口包含的方法
- 代理类的类型是 `com.sun.proxy.$Proxy0` 

## 1 `Proxy.newProxyInstance`源码分析
```
    public static Object newProxyInstance(ClassLoader loader,
                                          Class<?>[] interfaces,
                                          InvocationHandler h)
        throws IllegalArgumentException
    {
        // 判空
        Objects.requireNonNull(h);

        final Class<?>[] intfs = interfaces.clone();
        // 获取java应用的安全管理器
        final SecurityManager sm = System.getSecurityManager();
        if (sm != null) {
            // 如果启用了安全管理器，检查是否允许应用生成代理类
            // 调 getProxyClass0 方法前，必须进行 生成代理类的权限检查
            checkProxyAccess(Reflection.getCallerClass(), loader, intfs);
        }

        /*
         * Look up or generate the designated proxy class.
         */
         // 核心方法，生成代理类的类型对象：class com.sun.proxy.$Proxy0
        Class<?> cl = getProxyClass0(loader, intfs);

        /*
         * Invoke its constructor with the designated invocation handler.
         */
         // 根据生成的 class 通过反射获取构造函数对象并生成代理类实例
        try {
            if (sm != null) {
                checkNewProxyPermission(Reflection.getCallerClass(), cl);
            }
            // 获取代理类的带参构造函数，这里参数是 { InvocationHandler.class } 类型的，
            // 可见本方法中的 h 参数正是作为代理类的构造函数参数。
            final Constructor<?> cons = cl.getConstructor(constructorParams);
            final InvocationHandler ih = h;
            if (!Modifier.isPublic(cl.getModifiers())) {
                AccessController.doPrivileged(new PrivilegedAction<Void>() {
                    public Void run() {
                        cons.setAccessible(true);
                        return null;
                    }
                });
            }
            // 通过类对象的构造函数来生成类的实例
            return cons.newInstance(new Object[]{h});
        } catch (IllegalAccessException|InstantiationException e) {
            throw new InternalError(e.toString(), e);
        } catch (InvocationTargetException e) {
            Throwable t = e.getCause();
            if (t instanceof RuntimeException) {
                throw (RuntimeException) t;
            } else {
                throw new InternalError(t.toString(), t);
            }
        } catch (NoSuchMethodException e) {
            throw new InternalError(e.toString(), e);
        }
    }
```
## 1.1 `getProxyClass0(loader, intfs)`源码分析
```
    /**
     * Generate a proxy class.  Must call the checkProxyAccess method
     * to perform permission checks before calling this.
     */
     // 生成代理类
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        // 如果代理类的缓存变量 proxyClassCache 里有 interfaces 的代理类，则直接返回
        // 否则通过
        return proxyClassCache.get(loader, interfaces);
    }
```