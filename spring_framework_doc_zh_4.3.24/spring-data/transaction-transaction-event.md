# 17.8 事务边界事件

从Spring 4.2开始，事件的监听器可以绑定到事务的一个阶段。典型的例子是在事务成功完成时处理事件：当当前事务的结果对于监听器实际上很重要时，这允许更灵活地使用事件。

通过`@EventListener`注释完成注册常规事件侦听器。如果需要将其绑定到事务使用`@TransactionalEventListener`。执行此操作时，默认情况下，侦听器将绑定到事务的提交阶段。

我们举一个例子来说明这个概念。假设一个组件发布了一个订单创建的事件，我们想要定义一个只应该在已经发布它的事务成功提交后才处理该事件的监听器：

```java
@Component
public class MyComponent {

    @TransactionalEventListener
    public void handleOrderCreatedEvent(CreationEvent<Order> creationEvent) {
        ...
    }
}
```

该`TransactionalEventListener`注释暴露了一个`phase`属性，使我们能够定制其交易的阶段，听者应绑定到。有效的阶段是`BEFORE_COMMIT`，`AFTER_COMMIT`（默认值），`AFTER_ROLLBACK`以及`AFTER_COMPLETION`一个聚合的交易完成后（无论是提交或回滚）。

如果没有正在运行的事务，则根本不调用侦听器，因为我们无法遵守所需的语义。但是，可以通过将`fallbackExecution`注释的属性设置为来覆盖该行为`true`。

