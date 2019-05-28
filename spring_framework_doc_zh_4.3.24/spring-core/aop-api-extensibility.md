# 12.11 定义一个新的通知类型

Spring AOP旨在可扩展。虽然拦截实现策略目前在内部使用，但除了围绕建议的开箱即用拦截之外，还可以支持任意建议类型，之前，抛出建议和返回建议之后。

该`org.springframework.aop.framework.adapter`软件包是一个SPI软件包，允许在不改变核心框架的情况下添加对新的自定义建议类型的支持。自定义`Advice`类型的唯一约束是它必须实现 `org.aopalliance.aop.Advice`标记接口。

`org.springframework.aop.framework.adapter`有关更多信息，请参阅javadocs。