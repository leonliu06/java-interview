## 1 前言
&emsp;#emsp;`CGLIB`是一个操作字节码的库，提供了很多针对字节码操作的方法。`CGLIB`有一个`Enhancer`类，可以用来生成动态代理类。
## 2 `Enhancer`源码阅读
### 2.1 `setSuperClass`方法
```
    // 将目标类的类型（可以是接口）设置为代理类的父类，可见CGLIB代理类继承目标类，所有目标类不能为final
    public void setSuperclass(Class superclass) {
        if (superclass != null && superclass.isInterface()) {
            // 设置接口
            setInterfaces(new Class[]{ superclass });
        } else if (superclass != null && superclass.equals(Object.class)) {
            // affects choice of ClassLoader
            // 这里在可以知道不能对Object生成代理
            this.superclass = null;
        } else {
            // 变量superclass保存目标类型
            this.superclass = superclass;
        }
    }
```
### 2.2 `setCallback`方法
```
    // 设置单个回调对象
    public void setCallback(final Callback callback) {
        setCallbacks(new Callback[]{ callback });
    }
    
    // 用Callback数组变量 callbacks 保存回调对象
    public void setCallbacks(Callback[] callbacks) {
        // 回调对象不能为空
        if (callbacks != null && callbacks.length == 0) {
            throw new IllegalArgumentException("Array cannot be empty");
        }
        this.callbacks = callbacks;
    }        
```
### 2.3 `create()`方法
```
    public Object create() {
        classOnly = false;
        argumentTypes = null;
        return createHelper();
    }
    
    private Object createHelper() {
        // 前期校验
        preValidate();
        // KEY_FACTORY 是一个由抽象类 KeyFactory 通过静态工厂方法 create 生成的 EnhancerKey
        // EnhancerKey KEY_FACTORY = (EnhancerKey)KeyFactory.create(EnhancerKey.class, KeyFactory.HASH_ASM_TYPE, null);
        Object key = KEY_FACTORY.newInstance((superclass != null) ? superclass.getName() : null,
                ReflectUtils.getNames(interfaces),
                filter == ALL_ZERO ? null : new WeakCacheKey<CallbackFilter>(filter),
                callbackTypes,
                useFactory,
                interceptDuringConstruction,
                serialVersionUID);
        this.currentKey = key;
        Object result = super.create(key);
        return result;
    }    
```