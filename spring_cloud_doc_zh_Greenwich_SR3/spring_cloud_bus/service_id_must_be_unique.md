# 46. Service ID Must Be Unique

## 46.服务ID必须唯一

总线尝试两次以消除对事件的处理-一次来自原始事件 `ApplicationEvent`，一次来自队列。为此，它将对照当前服务ID检查发送服务ID。如果服务的多个实例具有相同的ID，则不会处理事件。在本地计算机上运行时，每个服务都在不同的端口上，并且该端口是ID的一部分。Cloud Foundry提供了一个区分索引。为确保该ID在Cloud Foundry之外是唯一的，请`spring.application.index`为每个服务实例设置唯一的ID 。