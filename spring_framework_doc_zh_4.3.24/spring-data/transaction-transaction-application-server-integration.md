# 17.9 与应用服务器集成

Spring的事务抽象通常是与应用程序服务器无关的。此外，Spring的`JtaTransactionManager`类可以选择对JTA `UserTransaction`和`TransactionManager`对象执行JNDI查找，自动检测后一个对象的位置，该位置因应用程序服务器而异。访问JTA `TransactionManager`允许增强的事务语义，特别是支持事务暂停。有关`JtaTransactionManager`详细信息，请参阅javadocs。

Spring `JtaTransactionManager`是在Java EE应用程序服务器上运行的标准选择，并且已知可在所有常见服务器上运行。诸如事务暂停之类的高级功能也适用于许多服务器 - 包括GlassFish，JBoss和Geronimo - 无需任何特殊配置。但是，对于完全支持的事务挂起和进一步的高级集成，Spring为WebLogic Server和WebSphere提供了特殊的适配器。以下各节将讨论这些适配器。

*对于标准方案（包括WebLogic Server和WebSphere），请考虑使用方便的<tx:jta-transaction-manager/>配置元素。*配置后，此元素会自动检测基础服务器并选择可用于平台的最佳事务管理器。这意味着您不必显式配置特定于服务器的适配器类（如以下部分所述）; 相反，它们是自动选择的，标准 `JtaTransactionManager`为默认回退。

### 17.9.1 IBM WebSphere

在WebSphere 6.1.0.9及更高版本中，推荐使用的Spring JTA事务管理器是 `WebSphereUowTransactionManager`。此特殊适配器利用IBM的`UOWManager`API，该API在WebSphere Application Server 6.1.0.9及更高版本中可用。使用此适配器，`PROPAGATION_REQUIRES_NEW`IBM正式支持Spring驱动的事务挂起（由启动的挂起/恢复）。

### 17.9.2 Oracle WebLogic Server

在WebLogic Server 9.0或更高版本上，您通常会使用 `WebLogicJtaTransactionManager`而不是库存`JtaTransactionManager`类。这个特殊的WebLogic特定子类`JtaTransactionManager`在WebLogic管理的事务环境中支持Spring的事务定义的全部功能，超出了标准的JTA语义：功能包括事务名称，每事务隔离级别以及在所有情况下正确恢复事务。