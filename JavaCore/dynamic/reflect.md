# 反射

##  背景

自定义或已有的类，都是 java.lang.Object 的子类，在 JVM 里，不管里面的类从那里来，都是一种实例对象，编码只是其中一种，通过运行时创建也是可以的。



##  步骤

1.  获取 Class 对象

三种方式，分别对应三种场景


2.  类信息

属性、方法、枚举和数组，整个的全部信息，比如：修饰符、方法的返回值和参数、异常列表等

3.  操作

修改上述内容值，或调用方法

