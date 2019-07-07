# 17.10 常见问题

### 17.10.1对特定DataSource使用错误的事务管理器

根据您选择的交易技术和要求使用*正确的* `PlatformTransactionManager`实施方案。如果使用得当，Spring Framework只提供了简单易用的抽象。如果使用全局事务，则*必须*使用 `org.springframework.transaction.jta.JtaTransactionManager`该类（或其[特定](transaction.html#transaction-application-server-integration)于 [应用程序服务器的子类](transaction.html#transaction-application-server-integration)）进行所有事务操作。否则，事务基础结构会尝试对容器`DataSource` 实例等资源执行本地事务。这样的本地事务没有意义，一个好的应用程序服务器将它们视为错误。