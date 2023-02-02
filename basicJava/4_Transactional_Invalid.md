# 声明式事务失效的场景

1. @Transactional应用在非public修饰的方法上。

2. @Transactional注解属性propagation设置错误。

3. @Transactional注解属性rollbackFor设置错误。

4. 在同一个类中调用方法，导致@Transactional失效 。

5. 异常被catch捕获导致@Transactional失效。

6. 数据库引擎不支持事务。
