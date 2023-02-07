# Stream

## 特性及优点 
* 无存储。Stream 不是一种数据结构，它只是某种数据源的一个视图，数据源可以是一个数组、Java 容器或 I/O channel 等。
* 为函数式编程而生。对 Stream的任何修改都不会修改背后的数据源，比如对Stream执行过滤操作并不会删除被过滤的元素，而是会产生一个不包含被过滤元素的新 Stream。
* 惰式执行。Stream 上的操作并不会立即执行，只有等到用户真正需要结果的时候才会执行。
* 可消费性。Stream 只能被“消费”一次，一旦遍历过就会失效，就像容器的迭代器那样，想要再次遍历必须重新生成一个新的容器。

## 创建
```java
Stream<String> stream =  list.stream();
Stream<String> stream = Stream.of("xxx", "yyy");
```

## 中间操作
| 流操作      | 目的                 | 入参         |
|:---------|:-------------------|:-----------|
| filter   | 使用给定的Predicate进行过滤 | Predicate  |
| map      | 处理元素并进行转换          | Function   |
| limit    | 限制结果的条数            | int        |
| skip     | 跳过前n个元素            | int        |
| sorted   | 在流内部对元素进行排序        | Comparator |
| distinct | 移除重复的元素            ||

```java
List<String> strings = Arrays.asList("xxx", "yyy");
String s = strings.stream().filter(s -> s.length() <= 4).map(String::length).sorted().limit(1).distinct();
```

## 最终操作
|流操作| 目的          |入参|
|:----|:------------|:----|
|foreach| 迭代处理流中的每个数据 |Consumer|
|count| 统计元素的条数     |
|collect|将流中的元素汇总到一个指定的集合中|

