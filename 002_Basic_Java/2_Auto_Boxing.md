## 自动拆装箱原理

自动装箱都是通过包装类的 valueOf() 方法来实现的.自动拆箱都是通过包装类对象的 xxxValue() 来实现的。

## 哪些地方会自动拆装箱
### 将基本数据类型放入集合类
```java
    List<Integer> li = new ArrayList<>();
    for (int i = 1; i < 50; i ++){
        li.add(i);
    }
```
### 包装类型和基本类型的大小比较

```java
    Integer a = 1;
    System.out.println(a == 1 ? "等于" : "不等于");
    Boolean bool = false;
    System.out.println(bool ? "真" : "假");
```

### 包装类型的运算

```java
    Integer i = 10;
    Integer j = 20;

    System.out.println(i+j);
```

### 三目运算符的使用
```java
    boolean flag = true;
    Integer i = 0;
    int j = 1;
    int k = flag ? i : j;
```
flag ? i : j; 片段中，第二段的 i 是一个包装类型的对象，而第三段的 j 是一个基本类型，所以会对包装类进行自动拆箱。如果这个时候 i 的值为 null，那么就会发生 NPE

### 函数参数与返回值
```java
    //自动拆箱
    public int getNum1(Integer num) {
     return num;
    }
    //自动装箱
    public Integer getNum2(int num) {
     return num;
    }

```

### 自动拆装箱与缓存

当需要进行自动装箱时，如果数字在 -128 至 127 之间时，会直接使用缓存中的对象，而不是重新创建一个对象。

其中的 Javadoc 详细的说明了缓存支持 -128 到 127 之间的自动装箱过程。最大值 127 可以通过 -XX:AutoBoxCacheMax=size 修改。

在 Boxing Conversion 部分的 Java 语言规范(JLS)规定如下：

如果一个变量 p 的值是：

-128 至 127 之间的整数 (§3.10.1)
true 和 false 的布尔值 (§3.10.3)
\u0000 至 \u007f 之间的字符 (§3.10.4)
范围内的时，将 p 包装成 a 和 b 两个对象时，可以直接使用 a == b 判断 a 和 b 的值是否相等。

