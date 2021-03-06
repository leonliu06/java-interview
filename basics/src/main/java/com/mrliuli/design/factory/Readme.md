## 1 三种工厂模式依次进一步抽象
```
简单工厂模式 ---> 工厂方法模式 ---> 抽象工厂模式
```

- 工厂方法模式只生产一类产品，如金属工厂只生产镍、铁等，同种类称为同等级，也就是说工厂方法模式只考虑生产同等级的产品；
- 抽象工厂模式将考虑多种类产品的生产，将同一个工厂所生产的不同种类的一级产品称为一个产品族；

## 2 关于工厂模式的一些问题和思考：
### 2.1 工厂模式如何解释这种错觉：“还不如直接new一个对象来的方便，有效”?
> 参考：https://blog.csdn.net/qq_36186690/article/details/82945749
 
&emsp;&emsp;产生这种错觉，说明在用工厂模式的地方可能用得并不合适，或不好，或者没有真正理解到底在什么场景下使用工厂模式，以及使用工厂模式的目的。
&emsp;&emsp;工厂模式适用的场景：
- 首先，工厂模式是为了解耦：把对象的创建和使用过程分开。就是`Class A`想调用`Class B`，那么 `A` 只是调用 `B` 的方法，而至于 `B` 的实例化，交给工厂类。
- 其次，如果创建 `B` 对象的过程都很复杂，且很多地方用到，那么就可以将创建 `B` 对象的过程交给工厂类来统一管理。既减少了代码重复，又便于维护管理。
例如，想把所有调用 `B` 的地方改成 `B` 的子类 `B1`，只需要在对应生产 `B` 的工厂类中修改一处就可以了。  

&emsp;&emsp;使用工厂模式的例子：
数据库工厂类

### 2.2 工厂模式（factory Method）的本质是什么？为什么引入工厂模式？
> 参考：https://www.zhihu.com/question/42975862/answer/1238536686

> 只考虑Factory Method模式的话，它的本质其实就是一个Template Method模式，只不过用在创建对象上。基本上就是同一套搞法（解决方案），用在创建对象实例时就是“工厂方法模式”，用在其他业务逻辑上时，就是“模版方法模式”。 