package com.mrliuli.design.singleton.enumsingleton;

/**
 * Created by liuli on 2019/09/24.
 *
 * 枚举实现单例。
 * 注：命令 javap -p Singleton 反编译得到：
 * public final class com.mrliuli.design.singleton.enumsingleton.Singleton extends java.lang.Enum<com.mrliuli.design.singleton.enumsingleton.Singleton> {
 *   public static final com.mrliuli.design.singleton.enumsingleton.Singleton INSTANCE;
 *   private java.lang.Object data;
 *   private static final com.mrliuli.design.singleton.enumsingleton.Singleton[] $VALUES;
 *   public static com.mrliuli.design.singleton.enumsingleton.Singleton[] values();
 *   public static com.mrliuli.design.singleton.enumsingleton.Singleton valueOf(java.lang.String);
 *   private com.mrliuli.design.singleton.enumsingleton.Singleton();
 *   public static com.mrliuli.design.singleton.enumsingleton.Singleton getInstance();
 *   public java.lang.Object getData();
 *   public void setData(java.lang.Object);
 *   static {};
 * }
 *
 * 说明 枚举实现单例也是通过 static 来保证单例的（在类加载的时候就初始化了）
 *
 */
public enum Singleton {

    /**
     * 单例对象
     */
    INSTANCE;

    public static Singleton getInstance() {
        return INSTANCE;
    }

    private Object data;

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

}
