# 17.MBean

### 介绍

Tomcat使用JMX MBean作为实现Tomcat可管理性的技术。

每个包中的mbeans-descriptors.xml文件中都有Catalina的JMX MBean的描述。

您将需要为自定义组件添加MBean描述，以避免出现“找不到ManagedBean”异常。

### 添加MBean描述

您也可以在mbeans-descriptors.xml文件中添加自定义组件的MBean描述，该文件与它描述的类文件位于同一包中。

mbeans-descriptors.xml的允许语法由[DTD](http://tomcat.apache.org/tomcat-9.0-doc/mbeans-descriptors.dtd)文件定义。

自定义LDAP身份验证领域的条目可能如下所示：

```xml
  <mbean         name="LDAPRealm"
            className="org.apache.catalina.mbeans.ClassNameMBean"
          description="Custom LDAPRealm"
               domain="Catalina"
                group="Realm"
                 type="com.myfirm.mypackage.LDAPRealm">

    <attribute   name="className"
          description="Fully qualified class name of the managed object"
                 type="java.lang.String"
            writeable="false"/>

    <attribute   name="debug"
          description="The debugging detail level for this component"
                 type="int"/>
    .
    .
    .

  </mbean>
```