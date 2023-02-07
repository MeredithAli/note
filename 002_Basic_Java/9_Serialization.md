# 序列化
## 相关接口和类

* java.io.Serializable
* java.io.Externalizable
* ObjectOutput
* ObjectInput
* ObjectOutputStream
* ObjectInputStream

### Serialization
类通过实现java.io.Serializable接口以启用其序列化功能。未实现此接口的类将无法使其任何状态序列化或反序列化。可序列化类的所有子类型本身都是可序列化的。
序列化接口没有方法或字段，仅用于标识可序列化的语义。

当试图对一个对象进行序列化时，如果遇到不支持Serializable接口的对象，那么将抛出NotSerializableException。

如果要序列化的类有父类，同时要持久化在父类中定义过的变量，那么父类也应该继承 java.io.Serializable接口。

### Externalizable接口
Externalizable继承了Serializable，该接口中定义了writeExternal()和readExternal()两个抽象方法。
当使用Externalizable接口进行序列化与反序列化时，开发人员需要重写writeExternal()与readExternal()方法

还有一点值得注意:在使用Externalizable接口进行序列化时，在读取对象时，会调用被序列化类的无参构造器去创建一个新的对象，
然后将被保存对象的字段的值分别填充到新对象中。所以，实现Externalizable接口的类必须提供一个public的无参的构造器。

### Tips

1. 如果一个类想被序列化，则需要实现Serializable接口，否则将抛出NotSerializable- Exception异常，这是因为在序列化操作过程中会对类的类型进行检查，要求被序列化的类必须属于Enum、Array和Serializable类型中的任何一种。
2. 在变量声明前加上关键字transient，可以阻止该变量被序列化到文件中。
3. 在类中增加writeObject和readObject方法可以实现自定义的序列化策略。

## serialVersionUID
serialVersionUID是用来验证版本一致性的，在做兼容性升级时，不要改变类中serialVersionUID的值。

如果一个类实现了Serializable接口，那么一定要记得定义serialVersionUID，否则会发生异常。可以在IDEA中通过调节配置让其提示，并且可以一键快速生成一个serialVersionUID 

之所以会发生异常，是因为反序列化过程中做了校验，并且如果没有明确定义 serialVersionUID，则会根据类的属性自动生成一个serialVersionUID

## 序列化破坏单例模式

序列化会通过反射调用无参数的构造方法创建一个新的对象。

解决方案：在Singleton类中定义readResolve，并在该方法中指定要反悔的对象的生成策略，就可以防止单例模式被破坏

```java
public class Singleton implements Serializable{
    private volatile static Singleton singleton; 
    private Singleton (){}
    public static Singleton getsingleton() {
        if (singleton == null) {
            synchronized (Singleton.class) {
                if (singleton == null) {
                    singleton = new Singleton();
                }
            }
        }
        return singleton;
    }
    private Object readResolve() {
        return singleton;
    }
}
```
