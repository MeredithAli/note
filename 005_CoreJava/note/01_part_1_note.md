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