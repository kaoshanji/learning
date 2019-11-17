# 12.6 简单的代理定义

特别是在定义事务代理时，最终可能会有许多类似的代理定义。使用父bean和子bean定义以及内部bean定义可以产生更清晰，更简洁的代理定义。

首先为代理创建父，*模板*，bean定义：

```xml
<bean id="txProxyTemplate" abstract="true"
        class="org.springframework.transaction.interceptor.TransactionProxyFactoryBean">
    <property name="transactionManager" ref="transactionManager"/>
    <property name="transactionAttributes">
        <props>
            <prop key="*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

这永远不会被实例化，所以可能实际上是不完整的。然后，需要创建的每个代理只是一个子bean定义，它将代理的目标包装为内部bean定义，因为目标永远不会单独使用。

```xml
<bean id="myService" parent="txProxyTemplate">
    <property name="target">
        <bean class="org.springframework.samples.MyServiceImpl">
        </bean>
    </property>
</bean>
```

当然可以覆盖父模板的属性，例如在这种情况下，事务传播设置：

```xml
<bean id="mySpecialService" parent="txProxyTemplate">
    <property name="target">
        <bean class="org.springframework.samples.MySpecialServiceImpl">
        </bean>
    </property>
    <property name="transactionAttributes">
        <props>
            <prop key="get*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="find*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="load*">PROPAGATION_REQUIRED,readOnly</prop>
            <prop key="store*">PROPAGATION_REQUIRED</prop>
        </props>
    </property>
</bean>
```

请注意，在上面的示例中，我们已使用*abstract*属性将父bean定义显式标记为 *abstract*， [如前所述](beans.html#beans-child-bean-definitions)，因此实际上可能无法实例化它。默认情况下，应用程序上下文（但不是简单的bean工厂）会预先实例化所有单例。因此，重要的是（至少对于单例bean）如果你有一个（父）bean定义，你打算只将它用作模板，并且这个定义指定了一个类，你必须确保将*abstract* 属性设置为*true*，否则应用程序上下文实际上会尝试预先实例化它。