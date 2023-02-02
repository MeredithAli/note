# 使用集合需要注意的问题
## 初始化集合
### 不建议用双括号语法初始化集合
使用双括号语法创建并初始化集合会导致很多内部类被创建
### 替代方案
#### 使用Arrays工具类
```java
List<String> list = Arrays.asList("xxx", "xxxx", "xxxxx");
```
#### 使用Stream
```java
List<String> list = Stream.of("xxx", "yyy", "zzz").collect(Collectiors.toList());
```
#### 使用第三方工具类
Guava
```java
ImmutableMap.of("k1", "v1", "k2", "v2");
ImmutableList.of("a", "b", "c");
```
#### Java9内置方法
```java
List.of()
```

## 同步容器的操作的线程安全问题
### 同步容器：
* Vector、Stack和HashTable
* Collections类中提供的静态工厂方法创建的类

同步容器是通过加锁实现线程安全的，并且只能保证单独的操作是线程安全的，无法保证复合操作的线程安全性。同步容器的读和写操作之间会互相阻塞。
```java
public Object deleteLast(Vector v){
    int lastIndex = v.size() - 1;
    v.remove(lastIndex);
}
```
多线程中，remove()方法有可能抛出ArrayIndexOutOfBoundsException

可以尝试加锁：
```java
public Object deleteLast(Vector v){
    synchronized(v) {
        int lastIndex = v.size() - 1;
        v.remove(lastIndex);
    }
}
```
for循环时remove被多线程执行时，也要特别注意：
```java
for(int i = 0; i < v.size(); i++) {
    v.remove(i);
}
```
### 并发容器
并发容器是Java 5中提供的，主要用来代替同步容器。并发容器有更好的并发能力，而且其中的ConcurrentHashMap定义了线程安全的复合操作。
比如使用putIfAbsent()实现”若没有则添加“的功能，使用replace()实现替换的功能。这两个操作都是原子操作，可以保证线程安全。
