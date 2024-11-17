# 一些例子
## 1.联合索引第一个字段用范围不会走索引
```
EXPLAIN SELECT * FROM employees WHERE name > 'LiLei' AND age = 22 AND position ='manager';
```
![img_11.png](img_11.png)
联合索引第一个字段就用范围查找不会走索引，mysql内部可能觉得第一个字段就用范围，结果集应该很大，回表效率不高，还不
如就全表扫描。（放在最后一个就走了）

## 2.强制走索引
```
EXPLAIN SELECT * FROM employees force index(idx_name_age_position) WHERE name > 'LiLei' AND age = 22 AND position ='manager';
```
虽然使用了强制走索引让联合索引第一个字段范围查找也走索引，扫描的行rows看上去也少了点，但是最终查找效率不一定比全表
扫描高，因为回表效率不高。

实验：
```
‐‐ 关闭查询缓存
set global query_cache_size=0;
set global query_cache_type=0;
‐‐ 执行时间0.333s
SELECT * FROM employees WHERE name > 'LiLei';
‐‐ 执行时间0.444s
SELECT * FROM employees force index(idx_name_age_position) WHERE name > 'LiLei';
```

## 3.覆盖索引优化
```
EXPLAIN SELECT name,age,position FROM employees WHERE name > 'LiLei' AND age = 22 AND position ='manager';
```
![img_12.png](img_12.png)

## 4.in和or在表数据量比较大的情况会走索引，在表记录不多的情况下会选择全表扫描
```
EXPLAIN SELECT * FROM employees WHERE name in ('LiLei','HanMeimei','Lucy') AND age = 22 AND position='manager';
```
![img_13.png](img_13.png)

```
EXPLAIN SELECT * FROM employees WHERE (name = 'LiLei' or name = 'HanMeimei') AND age = 22 AND position='manager';
```
![img_14.png](img_14.png)

做一个小实验，将employees 表复制一张employees_copy的表，里面保留两三条记录
```
 EXPLAIN SELECT * FROM employees_copy WHERE name in ('LiLei','HanMeimei','Lucy') AND age = 22 AND position ='manager';
```
![img_15.png](img_15.png)
数据量很少，全表扫描会更快一点
```
EXPLAIN SELECT * FROM employees_copy WHERE (name = 'LiLei' or name = 'HanMeimei') AND age = 22 AND position ='manager';
```
![img_16.png](img_16.png)

## 5.like KK% 一般情况都会走索引
```
EXPLAIN SELECT * FROM employees WHERE name like 'LiLei%' AND age = 22 AND position ='manager';
```
![img_17.png](img_17.png)

```
EXPLAIN SELECT * FROM employees_copy WHERE name like 'LiLei%' AND age = 22 AND position ='manager';
```
![img_18.png](img_18.png)
### 索引下推
索引下推（Index Condition Pushdown，ICP）, like KK%其实就是用到了索引下推优化。

对于辅助的联合索引(name,age,position)，正常情况按照最左前缀原则，
```
SELECT * FROM employees WHERE name like 'LiLei%' AND age = 22 AND position ='manager'
```
这种情况只会走name字段索引，因为根据name字段过滤完，得到的索引行里的age和position是无序的，无法很好的利用索引。

在MySQL5.6之前的版本，这个查询只能在联合索引里匹配到名字是 'LiLei' 开头的索引，然后拿这些索引对应的主键逐个回表，到主键索
引上找出相应的记录，再比对age和position这两个字段的值是否符合。

MySQL 5.6引入了索引下推优化，可以在索引遍历过程中，对索引中包含的所有字段先做判断，过滤掉不符合条件的记录之后再回表，可
以有效地减少回表次数。使用了索引下推优化后，上面那个查询在联合索引里匹配到名字是 'LiLei' 开头的索引之后，同时还会在索引里过
滤age和position这两个字段，拿着过滤完剩下的索引对应的主键id再回表查整行数据。

索引下推会减少回表次数，对于innodb引擎的表索引下推只能用于二级索引，innodb的主键索引（聚簇索引）树叶子节点上保存的是全
行数据，所以这个时候索引下推并不会达到减少查询全行数据的效果。

### 为什么范围查找Mysql没有用索引下推优化？
估计应该是Mysql认为范围查找过滤的结果集过大，like KK% 在绝大多数情况来看，过滤后的结果集比较小，所以这里Mysql选择给 like
KK% 用了索引下推优化，当然这也不是绝对的，有时like KK% 也不一定就会走索引下推。

# Mysql如何选择合适的索引

```
 EXPLAIN select * from employees where name > 'a';
```
![img_19.png](img_19.png)

如果用name索引需要遍历name字段联合索引树，然后还需要根据遍历出来的主键值去主键索引树里再去查出最终数据，成本比全表扫描
还高，可以用覆盖索引优化，这样只需要遍历name字段的联合索引树就能拿到所有结果，如下：

```
 EXPLAIN select name,age,position from employees where name > 'a' ;
```
![img_20.png](img_20.png)

```
EXPLAIN select * from employees where name > 'zzz' ;
```
![img_21.png](img_21.png)

对于上面这两种 name>'a' 和 name>'zzz' 的执行结果，mysql最终是否选择走索引或者一张表涉及多个索引，mysql最
终如何选择索引，我们可以用trace工具来一查究竟，开启trace工具会影响mysql性能，所以只能临时分析sql使用，用
完之后立即关闭。

```
 set session optimizer_trace="enabled=on",end_markers_in_json=on; ‐‐开启trace
```

```
select * from employees where name > 'a' order by position;
SELECT * FROM information_schema.OPTIMIZER_TRACE;
```
![img_22.png](img_22.png)

会得到一个trace结果集，是一个json的感觉。

## trace的结果中一些关键字段

### step_preparation
第一阶段：SQL准备阶段，格式化sql
### join_optimization
第二阶段：SQL优化阶段（简单的优化）

会去掉一些无意义的字段（如1 = 1），或者调整联合索引的顺序（把搜索条件按照联合索引的顺序排序）

### condition_processing
条件处理

### table_dependencies
表依赖详情

### rows_estimation"
预估表的访问成本

```json
 {
  "table": "`employees`",
  "range_analysis": {
    "table_scan": {--全表扫描情况
      "rows": 10123,--扫描行数
      "cost": 2054.7--查询成本
    }
    /* table_scan */,
    "potential_range_indexes": [--查询可能使用的索引
      {
        "index": "PRIMARY",--主键索引
        "usable": false,
        "cause": "not_applicable"
      },
      {
        "index": "idx_name_age_position", ‐‐辅助索引
        "usable": true,
        "key_parts": [
          "name",
          "age",
          "position",
          "id"
        ] /* key_parts */
      }
    ] /* potential_range_indexes */,
    "setup_range_conditions": [
    ] /* setup_range_conditions */,
    "group_index_range": {
      "chosen": false,
      "cause": "not_group_by_or_distinct"
    } /* group_index_range */,
    "analyzing_range_alternatives": {‐‐分析各个索引使用成本
    "range_scan_alternatives": [
      {
        "index": "idx_name_age_position",
        "ranges": [
          "a < name" ‐‐索引使用范围
        ] /* ranges */,
        "index_dives_for_eq_ranges": true,
        "rowid_ordered": false, ‐‐使用该索引获取的记录是否按照主键排序
        "using_mrr": false,
        "index_only": false, ‐‐是否使用覆盖索引
        "rows": 5061, ‐‐索引扫描行数
        "cost": 6074.2, ‐‐索引使用成本
        "chosen": false, ‐‐是否选择该索引
        "cause": "cost"
      }
```

扫描行数少 不一定cost就少，有可能会有回表成本，索引下推成本

### considered_execution_plans
最终考虑的执行计划。

### best_access_path
最优访问路径

### considered_access_paths
最终选择的访问路径

# 常见sql深入优化

## Order by与Group by优化
### Case1：
![img_23.png](img_23.png)

利用最左前缀法则：中间字段不能断，因此查询用到了name索引，从key_len=74也能看出，age索引列用在排序过程中，因为Extra字段里没有using filesort

### Case2：
![img_24.png](img_24.png)

从explain的执行结果来看：key_len=74，查询使用了name索引，由于用了position进行排序，跳过了age，出现了Using filesort。

### Case3:
![img_25.png](img_25.png)

查找只用到索引name，age和position用于排序，无Using filesort。

### Case4:
![img_26.png](img_26.png)

和Case 3中explain的执行结果一样，但是出现了Using filesort，因为索引的创建顺序为name,age,position，但是排序的时候age和position颠倒位置了。

### Case5:
![img_27.png](img_27.png)

与Case 4对比，在Extra中并未出现Using filesort，因为age为常量，在排序中被优化，所以索引未颠倒， 不会出现Using filesort。

### Case 6：
![img_28.png](img_28.png)

虽然排序的字段列与索引顺序一样，且order by默认升序，这里position desc变成了降序，导致与索引的排序方式不同，从而产生Using filesort。Mysql8以上版本有降序索引可以支持该种查询方式。

(实际在8的版本试了一下，还是filesort)

### Case7：
![img_29.png](img_29.png)

对于排序来说，多个相等条件也是范围查询(找出了两个结果集，一起排序，后面的字段并不是有序的)

### Case8: 
![img_30.png](img_30.png)

数据量太大了，并且需要回表

可以用覆盖索引优化。

![img_31.png](img_31.png)

## 优化总结

1. MySQL支持两种方式的排序filesort和index，Using index是指MySQL扫描索引本身完成排序。index效率高，filesort效率低。
2. order by满足两种情况会使用Using index。
   1. order by语句使用索引最左前列。 
   2. 使用where子句与order by子句条件列组合满足索引最左前列。
3. 尽量在索引列上完成排序，遵循索引建立（索引创建的顺序）时的最左前缀法则。
4. 如果order by的条件不在索引列上，就会产生Using filesort。
5. 能用覆盖索引尽量用覆盖索引
6. group by与order by很类似，其实质是先排序后分组，遵照索引创建顺序的最左前缀法则。对于group by的优化如果不需要排序的可以加上order by null禁止排序。
7. 注意，where高于having，能写在where中的限定条件就不要去having限定了。

# Using filesort文件排序原理详解
using filesort 是把整个表（聚簇索引）load到内存中
## filesort文件排序方式
### 单路排序：
是一次性取出满足条件行的所有字段，然后在sort buffer中进行排序；用trace工具可以看到sort_mode信息里显示< sort_key, additional_fields >或者< sort_key,
packed_additional_fields >
### 双路排序（又叫回表排序模式）
是首先根据相应的条件取出相应的排序字段和可以直接定位行数据的行ID，然后在 sort buffer 中进行排序，排序完后需要再次取回其它需要的字段；
用trace工具可以看到sort_mode信息里显示< sort_key, rowid >

MySQL 通过比较系统变量 max_length_for_sort_data(默认1024字节) 的大小和需要查询的字段总大小来判断使用哪种排序模式。
* 如果 字段的总长度小于max_length_for_sort_data ，那么使用 单路排序模式；
* 如果 字段的总长度大于max_length_for_sort_data ，那么使用 双路排序模式。

### Example
```
EXPLAIN SELECT * FROM employees WHERE name = 'zhuge' ORDER BY position;
```

我们先看单路排序的详细过程：
1. 从索引name找到第一个满足 name = ‘zhuge’ 条件的主键 id
2. 根据主键 id 取出整行，取出所有字段的值，存入 sort_buffer 中
3. 从索引name找到下一个满足 name = ‘zhuge’ 条件的主键 id
4. 重复步骤 2、3 直到不满足 name = ‘zhuge’
5. 对 sort_buffer 中的数据按照字段 position 进行排序
6. 返回结果给客户端

我们再看下双路排序的详细过程：
1. 从索引 name 找到第一个满足 name = ‘zhuge’  的主键id
2. 根据主键 id 取出整行，把排序字段 position 和主键 id 这两个字段放到 sort buffer 中
3. 从索引 name 取下一个满足 name = ‘zhuge’  记录的主键 id
4. 重复 3、4 直到不满足 name = ‘zhuge’
5. 对 sort_buffer 中的字段 position 和主键 id 按照字段 position 进行排序
6. 遍历排序好的 id 和字段 position，按照 id 的值回到原表中取出 所有字段的值返回给客户端

###  "number_of_tmp_files": 3, 
‐‐使用临时文件的个数，这个值如果为0代表全部使用的sort_buffer内存排序，否则使用的磁盘文件排序

其实对比两个排序模式，单路排序会把所有需要查询的字段都放到 sort buffer 中，而双路排序只会把主键和需要排序的字段放到 sort buffer 中进行排序，然后再通过主键回到原表查询需要的字段。

如果 MySQL 排序内存 sort_buffer 配置的比较小并且没有条件继续增加了，可以适当把max_length_for_sort_data 配置小点，让优化器选择使用双路排序算法，可以在sort_buffer 中一次排序更
多的行，只是需要再根据主键回到原表取数据。

如果 MySQL 排序内存有条件可以配置比较大，可以适当增大 max_length_for_sort_data 的值，让优化器优先选择全字段排序(单路排序)，把需要的字段放到 sort_buffer 中，这样排序后就会直接从内存里返回查
询结果了。

所以，MySQL通过 max_length_for_sort_data 这个参数来控制排序，在不同场景使用不同的排序模式，从而提升排序效率。

注意，如果全部使用sort_buffer内存排序一般情况下效率会高于磁盘文件排序，但不能因为这个就随便增大sort_buffer(默认1M)，mysql很多参数设置都是做过优化的，不要轻易调整。

# 索引设计原则

## 1.代码先行，索引后上
一般应该等到主体业务功能开发完毕，把涉及到该表相关sql都要拿出来分析之后再建立索引。

## 2.联合索引尽量覆盖条件
比如可以设计一个或者两三个联合索引(尽量少建单值索引)，让每一个联合索引都尽量去包含sql语句里的where、order by、group by的字段，还要确保这些联合索引的字段顺序尽量满足sql查询的最左前缀原
则。

## 3.不要在小基数字段上建立索引
索引基数是指这个字段在表里总共有多少个不同的值，比如一张表总共100万行记录，其中有个性别字段，其值不是男就是女，那么该字段的基数就是2。 

如果对这种小基数字段建立索引的话，还不如全表扫描了，因为你的索引树里就包含男和女两种值，根本没法进行快速的二分查找，那用索引就没有太大的意义了。

一般建立索引，尽量使用那些基数比较大的字段，就是值比较多的字段，那么才能发挥出B+树快速二分查找的优势来。

## 4.长字符串我们可以采用前缀索引
尽量对字段类型较小的列设计索引，比如说什么tinyint之类的，因为字段类型较小的话，占用磁盘空间也会比较小，此时你在搜索的时候性能也会比较好一点。

当然，这个所谓的字段类型小一点的列，也不是绝对的，很多时候你就是要针对varchar(255)这种字段建立索引，哪怕多占用一些磁盘空间也是有必要的。

对于这种varchar(255)的大字段可能会比较占用磁盘空间，可以稍微优化下，比如针对这个字段的前20个字符建立索引，就是说，对这个字段里的每个值的前20个字符放在索引树里，类似于 KEY
index(name(20),age,position)。

此时你在where条件里搜索的时候，如果是根据name字段来搜索，那么此时就会先到索引树里根据name字段的前20个字符去搜索，定位到之后前20个字符的前缀匹配的部分数据之后，再回到聚簇索引提取出来
完整的name字段值进行比对。

但是假如你要是order by name，那么此时你的name因为在索引树里仅仅包含了前20个字符，所以这个排序是没法用上索引的， group by也是同理。所以这里大家要对前缀索引有一个了解。

## 5.where与order by冲突时优先where
在where和order by出现索引设计冲突时，到底是针对where去设计索引，还是针对order by设计索引？到底是让where去用上索引，还是让order by用上索引?

一般这种时候往往都是让where条件去使用索引来快速筛选出来一部分指定的数据，接着再进行排序。

因为大多数情况基于索引进行where筛选往往可以最快速度筛选出你要的少部分数据，然后做排序的成本可能会小很多。

## 6.基于慢sql查询做优化
可以根据监控后台的一些慢sql，针对这些慢sql查询做特定的索引优化。

关于慢sql查询不清楚的可以参考这篇文章：

链接：http://note.youdao.com/noteshare?id=c71f1e66b7f91dab989a9d3a7c8ceb8e&sub=0B91DF863FB846AA9A1CDDF431402C7B

