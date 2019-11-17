# 17.6 编程式事务管理编程

Spring Framework提供了两种程序化事务管理方法：

- 使用`TransactionTemplate`。
- `PlatformTransactionManager`直接 使用实现。

Spring团队通常建议`TransactionTemplate`进行程序化事务管理。第二种方法类似于使用JTA `UserTransaction`API，尽管异常处理不那么麻烦。

### 17.6.1使用TransactionTemplate

将`TransactionTemplate`采用相同的方法和其他Spring *模板*，如`JdbcTemplate`。它使用回调方法，使应用程序代码不必执行样板获取和释放事务资源，并产生意图驱动的代码，因为编写的代码仅关注开发人员想要做的事情。

正如您将在后面的示例中看到的那样，使用`TransactionTemplate`绝对将您与Spring的事务基础结构和API相结合。程序化事务管理是否适合您的开发需求是您必须自己做出的决定。

必须在事务上下文中执行并且将`TransactionTemplate`显式使用的应用程序代码 如下所示。作为应用程序开发人员，您编写一个`TransactionCallback`实现（通常表示为匿名内部类），其中包含您需要在事务上下文中执行的代码。然后，将自定义的实例传递`TransactionCallback`给上面显示的 `execute(..)`方法`TransactionTemplate`。

```java
public class SimpleService implements Service {

    // single TransactionTemplate shared amongst all methods in this instance
    private final TransactionTemplate transactionTemplate;

    // use constructor-injection to supply the PlatformTransactionManager
    public SimpleService(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);
    }

    public Object someServiceMethod() {
        return transactionTemplate.execute(new TransactionCallback() {
            // the code in this method executes in a transactional context
            public Object doInTransaction(TransactionStatus status) {
                updateOperation1();
                return resultOfUpdateOperation2();
            }
        });
    }
}
```

如果没有返回值，请使用`TransactionCallbackWithoutResult`带有匿名类的方便类，如下所示：

```java
transactionTemplate.execute(new TransactionCallbackWithoutResult() {
    protected void doInTransactionWithoutResult(TransactionStatus status) {
        updateOperation1();
        updateOperation2();
    }
});
```

回调中的代码可以通过调用`setRollbackOnly()`提供的`TransactionStatus`对象上的方法来回滚事务 ：

```java
transactionTemplate.execute(new TransactionCallbackWithoutResult() {

    protected void doInTransactionWithoutResult(TransactionStatus status) {
        try {
            updateOperation1();
            updateOperation2();
        } catch (SomeBusinessExeption ex) {
            status.setRollbackOnly();
        }
    }
});
```

#### 指定 事务 设置

您可以`TransactionTemplate`通过编程方式或配置方式指定事务设置，例如传播模式，隔离级别，超时等。`TransactionTemplate`默认情况下，实例具有 [默认的事务设置](transaction.html#transaction-declarative-txadvice-settings)。以下示例显示了特定事务设置的编程自定义`TransactionTemplate:`

```java
public class SimpleService implements Service {

    private final TransactionTemplate transactionTemplate;

    public SimpleService(PlatformTransactionManager transactionManager) {
        this.transactionTemplate = new TransactionTemplate(transactionManager);

        // the transaction settings can be set here explicitly if so desired
        this.transactionTemplate.setIsolationLevel(TransactionDefinition.ISOLATION_READ_UNCOMMITTED);
        this.transactionTemplate.setTimeout(30); // 30 seconds
        // and so forth...
    }
}
```

以下示例`TransactionTemplate`使用Spring XML配置定义了一些自定义事务设置。在`sharedTransactionTemplate`因为需要可再注入尽可能多的服务。

```xml
<bean id="sharedTransactionTemplate"
        class="org.springframework.transaction.support.TransactionTemplate">
    <property name="isolationLevelName" value="ISOLATION_READ_UNCOMMITTED"/>
    <property name="timeout" value="30"/>
</bean>
```

最后，`TransactionTemplate`类的实例是线程安全的，因为实例不保持任何会话状态。然而，`TransactionTemplate`实例*确实*保持配置状态，因此虽然许多类可以共享a的单个实例`TransactionTemplate`，但是如果类需要使用`TransactionTemplate`具有不同设置的类（例如，不同的隔离级别），则需要创建两个不同的`TransactionTemplate`实例。

### 17.6.2使用PlatformTransactionManager

您也可以`org.springframework.transaction.PlatformTransactionManager` 直接使用它来管理您的交易。只需通过`PlatformTransactionManager`bean引用将您正在使用的实现传递 给您的bean。然后，使用`TransactionDefinition`和`TransactionStatus`对象可以启动事务，回滚和提交。

```java
efaultTransactionDefinition def = new DefaultTransactionDefinition();
// explicitly setting the transaction name is something that can only be done programmatically
def.setName("SomeTxName");
def.setPropagationBehavior(TransactionDefinition.PROPAGATION_REQUIRED);

TransactionStatus status = txManager.getTransaction(def);
try {
    // execute your business logic here
}
catch (MyException ex) {
    txManager.rollback(status);
    throw ex;
}
txManager.commit(status);
```

