# IOC配置详解

## 1.容器概述

ApplicationContext是Spring IoC容器实现的代表，它负责实例化，配置
和组装Bean。容器通过读取配置元数据获取有关实例化、配置和组装哪
些对象的说明 。配置元数据可以使用XML、Java注解或Java代码来呈
现。它允许你处理应用程序的对象与其他对象之间的互相依赖关系。

### 配置元数据
* 使用xml的配置 - 简单、直观 适合入门
* 基于注解的配置: @Compont(@serivce @controller @repository)  @Autowride
  
  Spring 2.5 支持基于注解的元数据配置.    SSM框架开发中
  的使用
* 基于Java的配置:  @Confiration @Bean @Import
  
  从 Spring 3.0开始, 由Spring JavaConfig项目提供的功能已
  经成为Spring核心框架的一部分。因此，你可以使用Java配置来代替
  XML配置定义外部bean .
  从spring4.0开始支持springboot1.0  之后  springboot
  完全采用javaConfig的方式进行开发。

### 容器的实例化
对象在Spring容器创建完成的时候就已经创建完成，不是需要用的时候才创建

### 容器的使用
ApplicationContext是能够创建bean定义以及处理相互依赖关系的高级工厂接口，使用方
法T getBean(String name, Class<T> requiredType)获取容器实例。

ClassPathXmlApplicationContext/FileSystemXmlApplicationContext/AnnotationConfigApplicationContext

### 获取bean
* 通过类来获取 getBean(User.class)
* 通过bean的名字或者id来获取Bean (User) ioc.getBean("user)
* 通过名字+类型 ioc.getBean("user".User.class)

# xml 介绍

## bean的概述
### 命名bean

```xml
<bean class="com.tuling.entity.User" id="user" name="user2 user3,user4;us
 er5"></bean>
```

```xml
<alias name="user" alias="user6"></alias>
<!--为外部定义的bean起别名-->
<description></description>
<!--用来描述一个bean是干什么的-->
<import></import>
<!--引入外部的object-->
```

### 实例化bean
#### 使用构造器实例化 默认 无法干预实例化过程
#### 使用静态工厂方法实例化
```xml
    <bean class="com.alice.beans.Person" id = "person" factory-method="createPersonFactory">
```

#### 使用实例工厂方法实例化
    <bean class="com.alice.beans.Person" id = "person2" factory-bean="personFactory" factory-method="createPersonFactoryMethod">

# 依赖注入
## 基于setter方法的依赖注入
```xml
    <!--基于setter方法的依赖注入-->
    <bean class="com.alice.beans.User" id = "user6">
        <property name="id" value="1"></property>
        <property name="userName" value="cheshire"></property>
        <property name="realName" value="alice"></property>
    </bean>

        <!--基于构造函数的依赖注入
            1. 基于name属性设置构造函数参数
            2. 可以只有value属性，按照顺序
            3. 如果省略name，要注意顺序
            4. 如果参数顺序错乱可以使用name，也可以使用index设置参数下标
        -->
    <bean class="com.alice.beans.User" id = "user7">
        <constructor-arg name = "id" value="2"/>
        <constructor-arg name="userName" value="cheshire"/>
        <constructor-arg name="realName" value="alice"/>
    </bean>
```

基于setter方法的依赖注入，name属性对应的是set方法的名字。

比如方法名是setId，那property对应的名字就是id

## 复杂一点的依赖注入

### 引用外部Bean
```xml
    <!--引用外部Bean-->
    <bean class="com.alice.beans.Person" id = "person">
        <property name="id" value="1"/>
        <property name="name">
            <null></null>
        </property>
        <property name="gender" value=""/>
        <property name="wife" ref="wife"/>
    </bean>

    <bean class="com.alice.beans.Wife" id = "wife">
        <property name="age" value="27"/>
        <property name="name" value="alice"/>
    </bean>
```

### 内部bean
```xml
    <bean class="com.alice.beans.Person" id = "person-1">
        <property name="id" value="1"/>
        <property name="name">
            <null></null>
        </property>
        <property name="gender" value=""/>
        <property name="wife">
            <bean class="com.alice.beans.Wife" id = "wife">
                <property name="age" value="27"/>
                <property name="name" value="alice-1"/>
            </bean>
        </property>
    </bean>
```

### list

```xml
        <property name="hobbies">
            <list>
                <value>sing</value>
                <value>dance</value>
            </list>
        </property>
```

### map

```xml
        <property name="course">
            <map>
                <entry key="1" value="Java"/>
                <entry key="2" value="Oracle"/>
            </map>
        </property>
```

使用p命名空间简化基于setter属性注入xml配置

使用c命名空间简化基于构造函数的XML

### 使用depends-on属性
```xml
    <bean class="com.alice.beans.User" id="user" depends-on="wife"></bean>
    <bean class="com.alice.beans.Wife" id="wife"></bean>
```
控制bean的加载顺序，当一个bean想让另一个bean在它之前加载，可以设置depends-on

### 懒加载
```xml
    <bean class="com.alice.beans.Wife" id = "wife" lazy-init="true"></bean>
```

不会在spring 容器加载的时候加载bean，而是在使用的时候才会加载

### Bean的作用域

#### Singleton（单例）的作用域
默认值，同一个id始终只会创建一次bean
#### Prototype（原型）的作用域
每一次使用都会创建出一个bean

单例和多例会对于线程安全有影响

还有其他的作用域：request/session/application/websocket

### 自动注入
当一个对象中需要引用另外一个对象的时候，在之前的配置中我们都是通过property标签来进行手动配置的，
其实在spring中还提供了一个非常强大的功能就是自动装配，可以按照我们指定的规则进行配置，配置的方式有以下几种：

* default/no：不自动装配
* byName：按照名字进行装配，以属性名作为id去容器中查找组件，进行赋值，如果找不到则装配null 
* byType：按照类型进行装配,以属性的类型作为查找依据去容器中找到这个组件，如果有多个类型相同的bean对象，那么会报异常，如果找不到则装配null 
* constructor：按照构造器进行装配，先按照有参构造器参数的类型进行装配，没有就直接装配null；如果按照类型找到了多个，那么就使用参数名作为id继续匹
  配，找到就装配，找不到就装配null

当根据类型匹配到多个 可以使用 1. 设置某个bean为主要bean primary="true" 2. 设置不需要自动注入的bean autowire-candidate="false" 忽略自动注入

## 自定义bean的特性
### 生命周期回调
* 使用接口
  * 初始化：实现InitializingBean
  * 销毁：DisposableBean
  *  什么时候销毁：在spring容器关闭的时候 close()
  *  或者 使用ConfigurableApplicationContext.registerShutdownHook方法优雅的关闭
* 使用指定具体方法的方式实现生命周期的回调
  *  在对应的bean里面创建对应的两个方法 init‐method="init" destroy‐method="destroy"

## spring创建第三方bean对象
和自己的类没有区别

### 引用外部配置文件（xxx.properties)
在加载外部依赖文件的时候需要context命名空间，引用的时候使用$