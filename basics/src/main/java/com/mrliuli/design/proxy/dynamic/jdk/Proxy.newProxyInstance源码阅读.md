## 1 前言
&emsp;&emsp;JDK动态代理类是通过静态方法 `Proxy.newProxyInstance(ClassLoader loader, Class<?>[] interfaces, InvocationHandler h)` 基于java的反射原理动态生成的。
方法的三个参数和返回值分别是：
- 参数 loader 使用目标类的类加载器来生成代理
- 参数 interfaces 目标类实现的所有接口，所有 java 动态代理只能代理 实现了接口的目标类的方法。 代理类会代理目标类所有接口中的方法。
- 参数 h 只有一个 invoke 方法，代理类方法的调用先发送到 h 中的 invoke 方法。h 会作为代理类的构造函数参数来生成代理类实例。
- 返回 代理类，代理类也实现了 目标类的接口。

&emsp;&emsp;由其参数和返回值可知JDK生成动态代理类机制的特点为：
- 动态代理类是对目标类的接口中的方法进行代理，所以目标类必须实现接口
- 动态代理类也是实现了相同的接口，所以代理类只能执行目标类中接口包含的方法
- 代理类的类型是 `com.sun.proxy.$Proxy0` 

## 2 `Proxy.newProxyInstance`源码分析
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
            // 通过类对象的构造函数来生成类的实例，InvocationHandler 类型的参数 h 构造函数传入代理类中
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
## 2.1 `getProxyClass0(loader, intfs)`源码分析
```
    /**
     * Generate a proxy class.  Must call the checkProxyAccess method
     * to perform permission checks before calling this.
     */
     // 生成代理类的类型对象
    private static Class<?> getProxyClass0(ClassLoader loader,
                                           Class<?>... interfaces) {
        if (interfaces.length > 65535) {
            throw new IllegalArgumentException("interface limit exceeded");
        }

        // If the proxy class defined by the given loader implementing
        // the given interfaces exists, this will simply return the cached copy;
        // otherwise, it will create the proxy class via the ProxyClassFactory
        // 如果代理类的缓存变量 proxyClassCache 里有 interfaces 的代理类，则直接返回
        // 否则通过 ProxyClassFactory 来生成
        return proxyClassCache.get(loader, interfaces);
    }
```
## 2.2 `proxyClassCache.get(loader, interfaces)`
`private static final WeakCache<ClassLoader, Class<?>[], Class<?>> proxyClassCache = new WeakCache<>(new KeyFactory(), new ProxyClassFactory());`
从这里可以看出 proxyClassCache 是一个 `WeackCache`，且构造时初始化了 `subKeyFactory` 为 `KeyFactory`，`valueFactory` 为 `ProxyClassFactory`;
下面看一下 `proxyClassCache.get(loader, interfaces)`
```
    public V get(K key, P parameter) {
        Objects.requireNonNull(parameter);

        expungeStaleEntries();

        Object cacheKey = CacheKey.valueOf(key, refQueue);

        // lazily install the 2nd level valuesMap for the particular cacheKey
        ConcurrentMap<Object, Supplier<V>> valuesMap = map.get(cacheKey);
        if (valuesMap == null) {
            ConcurrentMap<Object, Supplier<V>> oldValuesMap
                = map.putIfAbsent(cacheKey,
                                  valuesMap = new ConcurrentHashMap<>());
            if (oldValuesMap != null) {
                valuesMap = oldValuesMap;
            }
        }

        // create subKey and retrieve the possible Supplier<V> stored by that
        // subKey from valuesMap
        Object subKey = Objects.requireNonNull(subKeyFactory.apply(key, parameter));
        Supplier<V> supplier = valuesMap.get(subKey);
        Factory factory = null;

        while (true) {
            if (supplier != null) {
                // supplier might be a Factory or a CacheValue<V> instance
                // 核心是这里，下面跟踪一下这个方法
                V value = supplier.get();
                if (value != null) {
                    return value;
                }
            }
            // else no supplier in cache
            // or a supplier that returned null (could be a cleared CacheValue
            // or a Factory that wasn't successful in installing the CacheValue)

            // lazily construct a Factory
            if (factory == null) {
                factory = new Factory(key, parameter, subKey, valuesMap);
            }

            if (supplier == null) {
                supplier = valuesMap.putIfAbsent(subKey, factory);
                if (supplier == null) {
                    // successfully installed Factory
                    supplier = factory;
                }
                // else retry with winning supplier
            } else {
                if (valuesMap.replace(subKey, supplier, factory)) {
                    // successfully replaced
                    // cleared CacheEntry / unsuccessful Factory
                    // with our Factory
                    supplier = factory;
                } else {
                    // retry with current supplier
                    supplier = valuesMap.get(subKey);
                }
            }
        }
    }
```
## 2.3 `V value = supplier.get();` 

```
        @Override
        public synchronized V get() { // serialize access
            // re-check
            Supplier<V> supplier = valuesMap.get(subKey);
            if (supplier != this) {
                // something changed while we were waiting:
                // might be that we were replaced by a CacheValue
                // or were removed because of failure ->
                // return null to signal WeakCache.get() to retry
                // the loop
                return null;
            }
            // else still us (supplier == this)

            // create new value
            V value = null;
            try {
                // 这里 调用了 ProxyClassFactory 的  apply 方法
                value = Objects.requireNonNull(valueFactory.apply(key, parameter));
            } finally {
                if (value == null) { // remove us on failure
                    valuesMap.remove(subKey, this);
                }
            }
            // the only path to reach here is with non-null value
            assert value != null;

            // wrap value with CacheValue (WeakReference)
            CacheValue<V> cacheValue = new CacheValue<>(value);

            // put into reverseMap
            reverseMap.put(cacheValue, Boolean.TRUE);

            // try replacing us with CacheValue (this should always succeed)
            if (!valuesMap.replace(subKey, this, cacheValue)) {
                throw new AssertionError("Should not reach here");
            }

            // successfully replaced us with new CacheValue -> return the value
            // wrapped by it
            return value;
        }
```
## 2.4 `valueFactory.apply(key, parameter)` 这里的 `valueFactory` 就是 `ProxyClassFactory` 类，是 `Proxy` 的 `private static final class`
```
        @Override
        public Class<?> apply(ClassLoader loader, Class<?>[] interfaces) {

            Map<Class<?>, Boolean> interfaceSet = new IdentityHashMap<>(interfaces.length);
            // 这个 for 循环内的代码 对目标类的接口进行一系列校验
            for (Class<?> intf : interfaces) {
                /*
                 * Verify that the class loader resolves the name of this
                 * interface to the same Class object.
                 */
                 // 校验 类加载器 可以 解析 目标类 为 相同的 类型对象
                Class<?> interfaceClass = null;
                try {
                    interfaceClass = Class.forName(intf.getName(), false, loader);
                } catch (ClassNotFoundException e) {
                }
                if (interfaceClass != intf) {
                    throw new IllegalArgumentException(
                        intf + " is not visible from class loader");
                }
                /*
                 * Verify that the Class object actually represents an
                 * interface.
                 */
                 // 校验 目标类 是接口，这里也说明了 JDK 动态代理只能代理 实现了接口的 目标类
                if (!interfaceClass.isInterface()) {
                    throw new IllegalArgumentException(
                        interfaceClass.getName() + " is not an interface");
                }
                /*
                 * Verify that this interface is not a duplicate.
                 */
                 // 校验 目标类 实现的接口不能重复
                if (interfaceSet.put(interfaceClass, Boolean.TRUE) != null) {
                    throw new IllegalArgumentException(
                        "repeated interface: " + interfaceClass.getName());
                }
            }

            String proxyPkg = null;     // package to define proxy class in
            int accessFlags = Modifier.PUBLIC | Modifier.FINAL;

            /*
             * Record the package of a non-public proxy interface so that the
             * proxy class will be defined in the same package.  Verify that
             * all non-public proxy interfaces are in the same package.
             */
             // 如果 目标类接口中 存在方法是 非公有的，则 代理类包名称 就赋为 目标类接口的包名称，
             // 使其在 相同的包路径下，以使 代理类能够访问目标类的 非公有方法
             // 如果 目标类接口中，存在不同的包路径，则抛出异常。
            for (Class<?> intf : interfaces) {
                int flags = intf.getModifiers();
                if (!Modifier.isPublic(flags)) {
                    accessFlags = Modifier.FINAL;
                    String name = intf.getName();
                    int n = name.lastIndexOf('.');
                    String pkg = ((n == -1) ? "" : name.substring(0, n + 1));
                    if (proxyPkg == null) {
                        proxyPkg = pkg;
                    } else if (!pkg.equals(proxyPkg)) {
                        throw new IllegalArgumentException(
                            "non-public interfaces from different packages");
                    }
                }
            }
            // 如果没有 非公有方法，则代理类的包路径 使用 com.sun.proxy.
            if (proxyPkg == null) {
                // if no non-public proxy interfaces, use com.sun.proxy package
                proxyPkg = ReflectUtil.PROXY_PACKAGE + ".";
            }

            /*
             * Choose a name for the proxy class to generate.
             */
            long num = nextUniqueNumber.getAndIncrement();
            // 到这里，代理类包路径为 com.sun.proxy. + $Proxy + 0，即为 com.sun.proxy.$Proxy0
            String proxyName = proxyPkg + proxyClassNamePrefix + num;

            /*
             * Generate the specified proxy class.
             */
             // 这里生成 指定代理类的 字节码数组
            byte[] proxyClassFile = ProxyGenerator.generateProxyClass(
                proxyName, interfaces, accessFlags);
            try {
                // 读取字节码数组，并生成 class
                return defineClass0(loader, proxyName,
                                    proxyClassFile, 0, proxyClassFile.length);
            } catch (ClassFormatError e) {
                /*
                 * A ClassFormatError here means that (barring bugs in the
                 * proxy class generation code) there was some other
                 * invalid aspect of the arguments supplied to the proxy
                 * class creation (such as virtual machine limitations
                 * exceeded).
                 */
                throw new IllegalArgumentException(e.toString());
            }
        }
```
## 3 总结
JDK动态代理的目标类必须实现接口，如果接口中存在非公有访问属性的方法，则代理类的包路径采用目标类相同的包路径，以使得代理类能够访问到目标类的所有方法，
否则代理类的包路径名称为`com.sun.proxy.$Proxy0`。因为java可以实现多个接口，所以如果目标类的多个接口中有两个包含非公有方法的接口，且不在同一个包路径下，
那么将会抛出异常，不能生成代理类。生成代理类的方法 `Proxy.newProxyInstance` 中的第三个参数 `InvocationHandler h` 最终传给代理类。