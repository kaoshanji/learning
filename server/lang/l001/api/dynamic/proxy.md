# 代理

使用动态代理，可以在运行时动态创建出同时实现多个 Java 接口的代理类及其对象实例，而不需要在源代码中通过 implerments 关键词来声明。

动态代理可以将所有接口的调用重定向到调用处理器 InvocationHandler ，调用他的 invoke 方法，接口只是定义，对外暴露，具体的逻辑，是在 InvocationHandler 接口的实现类里








##  示例