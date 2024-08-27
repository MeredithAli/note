# 第四章
## 构造器，初始化块
```java
class Employee{
    private static int nextId;
    private int id;
    private String name;
    private double salary;
    {
        id = nextId;
        nextId++;
    }
    
    public Employee(String n, double s){
        name = n;
        salary = s;
    }
    
    public Employee() {
        name = "";
        salary = 0;
    }
    
}
```

构造过程处理步骤
1. 如果构造器的第一行调用了另一个构造器，则基于所提供的参数执行第二个构造器。
2. 否则

   a) 所有实例字段初始化为其默认值（0、false或null）。

   b) 按照在类声明中出现的顺序，执行所有字段初始化方法和初始化块。
3. 执行构造器主体代码

# 第八章 泛型
## 类型变量的限定

```java
public static <T extends Comparable> T min(T[] a)...
```

表示T应该是限定类型的子类型，T和限定类型可以是类，也可以是接口。一个类型变量或通配符可以有多个限定。

```java
T extends Comparable &Serializable
```

根据Java继承机制，可以根据需要拥有多个接口超类型，但是最多有一个限定可以是类。
如果有一个类作为限定，它必须是限定列表中的第一个限定。

## 类型擦除
无论何时定义一个泛型类型，都会自动提供一个相应的原始类型，这个原始类型的名字就是去掉类型参数后的泛型类型名。
类型变量会被擦除，并替换为其限定类型（或者，对于无限定的变量则替换为Object）。

原始类型用第一个限定来替换类型变量。为了提高效率，应该将标记接口（即没有方法的接口）放在限定列表的末尾。

* 虚拟机中没有泛型，只有普通的类和方法
* 所有的类型参数都会替换为他们的限定类型
* 会合成桥方法来保持多态
* 为保持类型安全性，必要时会插入强制类型转换

