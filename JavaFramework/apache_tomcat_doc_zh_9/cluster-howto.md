# 19.集群

### 对于急躁的人

只需添加

```xml
<Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"/>
```

到您`<Engine>`或您的`<Host>`元素以启用群集。

使用上述配置将启用使用`DeltaManager`复制会话增量的所有会话复制。从全部到全部，我们的意思是*每个* 会话都被复制到集群中的*所有其他节点*。这对于较小的群集非常有效，但是我们不建议对较大的群集（大约4个以上的节点）使用它。此外，在使用DeltaManager时，Tomcat会将会话复制到*所有*节点， *甚至没有部署应用程序的节点*。
要解决这些问题，您将需要使用`BackupManager`。该`BackupManager` 只复制会话数据，以*一个*备份节点，并且仅备份到已部署应用程序的节点上。一旦有一个运行了的简单集群`DeltaManager`，您可能会希望在`BackupManager`增加集群中的节点数时迁移到。

以下是一些重要的默认值：

1. 组播地址是228.0.0.4
2. 组播端口为45564（端口和地址共同决定群集成员资格。
3. 广播的IP是`java.net.InetAddress.getLocalHost().getHostAddress()`（确保您不广播127.0.0.1，这是一个常见错误）
4. 侦听复制消息的TCP端口是范围中的第一个可用服务器套接字 `4000-4100`
5. 侦听器已配置 `ClusterSessionListener`
6. 配置了两个拦截器`TcpFailureDetector`，`MessageDispatchInterceptor`

以下是默认的群集配置：

```xml
        <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"
                 channelSendOptions="8">

          <Manager className="org.apache.catalina.ha.session.DeltaManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"/>

          <Channel className="org.apache.catalina.tribes.group.GroupChannel">
            <Membership className="org.apache.catalina.tribes.membership.McastService"
                        address="228.0.0.4"
                        port="45564"
                        frequency="500"
                        dropTime="3000"/>
            <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                      address="auto"
                      port="4000"
                      autoBind="100"
                      selectorTimeout="5000"
                      maxThreads="6"/>

            <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
              <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
            </Sender>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor"/>
          </Channel>

          <Valve className="org.apache.catalina.ha.tcp.ReplicationValve"
                 filter=""/>
          <Valve className="org.apache.catalina.ha.session.JvmRouteBinderValve"/>

          <Deployer className="org.apache.catalina.ha.deploy.FarmWarDeployer"
                    tempDir="/tmp/war-temp/"
                    deployDir="/tmp/war-deploy/"
                    watchDir="/tmp/war-listen/"
                    watchEnabled="false"/>

          <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/>
        </Cluster>
```

我们将在本文档的后面部分更详细地介绍这一部分。

### 安全

在将安全，受信任的网络用于所有与群集相关的网络流量的基础上编写群集实现。在不安全，不受信任的网络上运行群集是不安全的。

有许多选项可提供安全，可信任的网络，以供Tomcat群集使用。这些包括：

- 专用局域网
- 虚拟专用网（VPN）
- IPSEC
- 使用[EncryptInterceptor](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-interceptor.html#org.apache.catalina.tribes.group.interceptors.EncryptInterceptor_Attributes)加密群集流量

### 集群基础

要在Tomcat 9容器中运行会话复制，应完成以下步骤：

- 您的所有会话属性都必须实现 `java.io.Serializable`
- 取消注释`Cluster`server.xml中的元素
- 如果定义了自定义集群阀，请确保`ReplicationValve` 在server.xml中的Cluster元素下也有定义
- 如果您的Tomcat实例在同一台机器上运行，请确保`Receiver.port` 每个实例的属性都是唯一的，在大多数情况下，Tomcat足够聪明，可以通过自动检测4000-4100范围内的可用端口自行解决此问题
- 确保你`web.xml`有 `<distributable/>`元素
- 如果您使用的是mod_jk，请确保在您的引擎上设置`<Engine name="Catalina" jvmRoute="node01" >` 了jvmRoute属性，并且jvmRoute属性值与worker.properties中的工作程序名称匹配。
- 确保所有节点都具有相同的时间并与NTP服务同步！
- 确保将您的负载均衡器配置为粘性会话模式。

负载均衡可以通过许多技术来实现，如“ [负载均衡”](http://tomcat.apache.org/tomcat-9.0-doc/balancer-howto.html)一章所示。

注意：请记住，您的会话状态是由Cookie跟踪的，因此您的URL必须从外到外看起来相同，否则，将创建一个新会话。

群集模块使用Tomcat JULI日志记录框架，因此您可以通过常规logging.properties文件配置日志记录。要跟踪消息，可以启用登录密钥：`org.apache.catalina.tribes.MESSAGES`

### 总览

要在Tomcat中启用会话复制，可以遵循三个不同的路径来实现完全相同的目的：

1. 使用会话持久性，并将会话保存到共享文件系统（PersistenceManager + FileStore）
2. 使用会话持久性，并将会话保存到共享数据库（PersistenceManager + JDBCStore）
3. 使用内存中复制，使用Tomcat附带的SimpleTcpCluster（lib / catalina-tribes.jar + lib / catalina-ha.jar）

Tomcat可以使用进行会话状态的全部复制，也可以使用`DeltaManager`进行备份复制到一个节点`BackupManager`。全部复制是仅在群集较小时才有效的算法。对于较大的群集，应使用BackupManager来使用主次会话复制策略，其中会话将仅存储在一个备份节点上。
当前，您可以使用域工作程序属性（mod_jk> 1.2.8）构建群集分区，并可能通过DeltaManager获得更具扩展性的群集解决方案（您需要为此配置域拦截器）。为了在所有环境中降低网络流量，可以将群集分成较小的组。通过为不同的组使用不同的多播地址，可以轻松实现这一点。一个非常简单的设置如下所示：

```bash
        DNS Round Robin
               |
         Load Balancer
          /           \
      Cluster1      Cluster2
      /     \        /     \
  Tomcat1 Tomcat2  Tomcat3 Tomcat4
```

这里要提到的重要一点是，会话复制仅仅是集群的开始。用于实现集群的另一个流行概念是耕种，即，您仅将应用程序部署到一台服务器，并且集群将在整个集群中分布部署。这是FarmWarDeployer可以使用的所有功能（位于的群集示例`server.xml`）。

在下一节中，将更深入地讨论会话复制的工作方式以及如何配置它。

### 集群信息

成员资格是使用多播心跳建立的。因此，如果您想细分集群，可以通过更改`<Membership>`元素中的多播IP地址或端口来实现。

心跳包含Tomcat节点的IP地址和Tomcat侦听复制流量的TCP端口。所有数据通信均通过TCP进行。

将`ReplicationValve`被用来找出当请求已经完成并开始复制，如果有的话。仅当会话已更改时才复制数据（通过在会话上调用setAttribute或removeAttribute）。

性能最重要的考虑因素之一是同步复制与异步复制。在同步复制模式下，直到复制的会话通过网络发送并在所有其他群集节点上重新实例化后，请求才返回。同步与异步使用`channelSendOptions` 标志配置，它是一个整数值。`SimpleTcpCluster/DeltaManager`组合的默认值为8，这是异步的。

为方便起见，`channelSendOptions`可以按名称（而不是整数）进行设置，然后在启动时将其转换为整数值。有效的选项名称是：“异步”（别名“异步”），“字节消息”（别名“字节”），“多播”，“安全”，“ synchronized_ack”（别名“ sync”），“ udp”，“ use_ack” ”。使用逗号分隔多个名称，例如，为options传递“异步，多播” `SEND_OPTIONS_ASYNCHRONOUS | SEND_OPTIONS_MULTICAST`。

您可以在[send标志（概述）](http://tomcat.apache.org/tomcat-9.0-doc/tribes/introduction.html)或 [send标志（javadoc）](https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/catalina/tribes/Channel.html)上阅读更多内容。在异步复制期间，将在复制数据之前返回请求。异步复制可缩短请求时间，同步复制可确保在请求返回之前复制会话。

### 崩溃后将会话绑定到故障转移节点

如果您使用的是mod_jk而不是使用粘性会话，或者由于某些原因粘性会话无法正常工作，或者您只是在进行故障转移，则需要修改会话ID，因为该会话ID之前包含了先前tomcat的worker ID（已定义）通过引擎元素中的jvmRoute）。为了解决这个问题，我们将使用JvmRouteBinderValve。

JvmRouteBinderValve重写会话ID，以确保故障转移后下一个请求将保持粘性（并且不会退回到随机节点，因为该工作器不再可用）。阀门用相同的名称重写Cookie中的JSESSIONID值。如果未安装此阀，则在mod_jk模块发生故障的情况下，将很难确保粘性。

请记住，如果要在server.xml中添加自己的阀门，则默认值不再有效，请确保添加了默认值定义的所有适当的阀门。

**提示：**
使用属性*sessionIdAttribute*可以更改包括旧会话ID的请求属性名称。默认属性名称是*org.apache.catalina.ha.session.JvmRouteOrignalSessionID*。

**技巧：**
将节点拖放到所有备份节点之前，可以通过JMX启用此mod_jk周转模式！在所有JvmRouteBinderValve备份上设置enable true，在mod_jk禁用worker，然后删除节点并重新启动它！然后启用mod_jk Worker并再次禁用JvmRouteBinderValves。该用例意味着仅迁移请求的会话。

### 配置实例

```xml
        <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"
                 channelSendOptions="6">

          <Manager className="org.apache.catalina.ha.session.BackupManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"
                   mapSendOptions="6"/>
          <!--
          <Manager className="org.apache.catalina.ha.session.DeltaManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"/>
          -->
          <Channel className="org.apache.catalina.tribes.group.GroupChannel">
            <Membership className="org.apache.catalina.tribes.membership.McastService"
                        address="228.0.0.4"
                        port="45564"
                        frequency="500"
                        dropTime="3000"/>
            <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                      address="auto"
                      port="5000"
                      selectorTimeout="100"
                      maxThreads="6"/>

            <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
              <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
            </Sender>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor"/>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.ThroughputInterceptor"/>
          </Channel>

          <Valve className="org.apache.catalina.ha.tcp.ReplicationValve"
                 filter=".*\.gif|.*\.js|.*\.jpeg|.*\.jpg|.*\.png|.*\.htm|.*\.html|.*\.css|.*\.txt"/>

          <Deployer className="org.apache.catalina.ha.deploy.FarmWarDeployer"
                    tempDir="/tmp/war-temp/"
                    deployDir="/tmp/war-deploy/"
                    watchDir="/tmp/war-listen/"
                    watchEnabled="false"/>

          <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/>
        </Cluster>
```

分解吧！

```xml
        <Cluster className="org.apache.catalina.ha.tcp.SimpleTcpCluster"
                 channelSendOptions="6">
```

在该元素内部，可以配置所有群集详细信息的主要元素。的`channelSendOptions`是，被附接到由SimpleTcpCluster类或调用的方法SimpleTcpCluster.send任何对象发送的每个消息的标志。发送标志的说明是可以在[ 我们网站的javadoc](https://tomcat.apache.org/tomcat-9.0-doc/api/org/apache/catalina/tribes/Channel.html) 的`DeltaManager`发送使用SimpleTcpCluster.send方法的信息，而备份管理器直接发送自己通过渠道。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster.html)

```xml
          <Manager className="org.apache.catalina.ha.session.BackupManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"
                   mapSendOptions="6"/>
          <!--
          <Manager className="org.apache.catalina.ha.session.DeltaManager"
                   expireSessionsOnShutdown="false"
                   notifyListenersOnReplication="true"/>
          -->
```

这是管理器配置的模板，如果<Context>元素中未定义管理器，将使用该模板。在Tomcat 5.x中，每个标记为可分发的Web应用程序都必须使用相同的管理器，不再是这种情况，因为Tomcat可以为每个Web应用程序定义一个管理器类，以便可以在群集中混合使用管理器。显然，一个节点的应用程序上的管理器必须与另一节点上的同一应用程序上的同一管理器相对应。如果未为该Web应用程序指定管理器，并且该Web应用程序标记为<distributable />，则Tomcat将采用该管理器配置并创建一个克隆该配置的管理器实例。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-manager.html)

```xml
          <Channel className="org.apache.catalina.tribes.group.GroupChannel">
```

通道元素是[Tribes](http://tomcat.apache.org/tomcat-9.0-doc/tribes/introduction.html)，这是Tomcat内部使用的组通信框架。该元素封装了与通信和成员资格逻辑有关的所有内容。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-channel.html)

```xml
            <Membership className="org.apache.catalina.tribes.membership.McastService"
                        address="228.0.0.4"
                        port="45564"
                        frequency="500"
                        dropTime="3000"/>
```

成员资格是使用多播完成的。请注意，`StaticMembershipInterceptor`如果您想将成员资格扩展到多播以外的点，则Tribes还支持使用的静态成员 资格。地址属性是使用的多播地址，端口是多播端口。这两个一起创建了群集分离。如果要QA群集和生产群集，最简单的配置是使QA群集位于与生产群集不同的单独的多播地址/端口组合上。
成员资格组件将自身的TCP地址/端口广播到其他节点，以便可以通过TCP进行节点之间的通信。请注意，正在广播的地址是 `Receiver.address`属性之一。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-membership.html)

```xml
            <Receiver className="org.apache.catalina.tribes.transport.nio.NioReceiver"
                      address="auto"
                      port="5000"
                      selectorTimeout="100"
                      maxThreads="6"/>
```

在部落中，发送和接收数据的逻辑分为两个功能组件。顾名思义，接收方负责接收消息。由于Tribes堆栈的线程较少（其他框架现在也采用了流行的改进），因此该组件中的线程池具有maxThreads和minThreads设置。
地址属性是将由成员资格组件广播到其他节点的主机地址。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-receiver.html)

```xml
            <Sender className="org.apache.catalina.tribes.transport.ReplicationTransmitter">
              <Transport className="org.apache.catalina.tribes.transport.nio.PooledParallelSender"/>
            </Sender>
```

顾名思义，发送方组件负责将消息发送到其他节点。发送者有一个shell组件，`ReplicationTransmitter`但实际的工作是在sub组件中完成的`Transport`。部落支持拥有一个发件人池，以便可以并行发送消息，如果使用NIO发件人，则也可以同时发送消息。
同时意味着一条消息同时发送给多个发件人，而并行意味着一条消息同时发送给多个发件人。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-sender.html)

```xml
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.TcpFailureDetector"/>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.MessageDispatchInterceptor"/>
            <Interceptor className="org.apache.catalina.tribes.group.interceptors.ThroughputInterceptor"/>
          </Channel>
```

部落使用堆栈来发送消息。堆栈中的每个元素都称为拦截器，其工作方式与Tomcat Servlet容器中的阀门非常相似。使用拦截器，可以将逻辑分解为更易于管理的代码段。上面配置的拦截器是：
TcpFailureDetector-通过TCP验证崩溃的成员，如果多播数据包被丢弃，则该拦截器可防止误报，即，即使该节点仍处于活动和运行状态，也标记为已崩溃。
MessageDispatchInterceptor-将消息调度到线程（线程池）以异步发送消息。
ThroughputInterceptor-输出有关邮件流量的简单统计信息。
请注意，拦截器的顺序很重要。它们在server.xml中的定义方式就是它们在通道堆栈中的表示方式。可以将其视为一个链表，头是第一个拦截器，尾号是最后一个。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-interceptor.html)

```xml
          <Valve className="org.apache.catalina.ha.tcp.ReplicationValve"
                 filter=".*\.gif|.*\.js|.*\.jpeg|.*\.jpg|.*\.png|.*\.htm|.*\.html|.*\.css|.*\.txt"/>
```

集群使用阀门来跟踪对Web应用程序的请求，我们在上面提到了ReplicationValve和JvmRouteBinderValve。<Cluster>元素本身不是Tomcat中管道的一部分，而是集群将阀门添加到其父容器中。如果在<Engine>元素中配置了<Cluster>元素，则将阀门添加到引擎中，依此类推。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-valve.html)

```xml
          <Deployer className="org.apache.catalina.ha.deploy.FarmWarDeployer"
                    tempDir="/tmp/war-temp/"
                    deployDir="/tmp/war-deploy/"
                    watchDir="/tmp/war-listen/"
                    watchEnabled="false"/>
```

默认的tomcat集群支持场部署，即集群可以在其他节点上部署和取消部署应用程序。该组件的状态目前处于变化之中，但很快就会解决。在Tomcat 5.0和5.5之间，部署算法发生了变化，此时，该组件的逻辑更改为部署目录必须与webapps目录匹配的位置。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-deployer.html)

```xml
          <ClusterListener className="org.apache.catalina.ha.session.ClusterSessionListener"/>
        </Cluster>
```

由于SimpleTcpCluster本身是Channel对象的发送者和接收者，因此组件可以将自己注册为SimpleTcpCluster的侦听器。上面的`ClusterSessionListener`侦听器侦听DeltaManager复制消息，并将增量应用到管理器，该管理器又将其应用到会话。
有关更多信息，请访问[参考文档](http://tomcat.apache.org/tomcat-9.0-doc/config/cluster-listener.html)

### 集群架构

**组件级别：**

```bash
         Server
           |
         Service
           |
         Engine
           |  \
           |  --- Cluster --*
           |
         Host
           |
         ------
        /      \
     Cluster    Context(1-N)
        |             \
        |             -- Manager
        |                   \
        |                   -- DeltaManager
        |                   -- BackupManager
        |
     ---------------------------
        |                       \
      Channel                    \
    ----------------------------- \
        |                          \
     Interceptor_1 ..               \
        |                            \
     Interceptor_N                    \
    -----------------------------      \
     |          |         |             \
   Receiver    Sender   Membership       \
                                         -- Valve
                                         |      \
                                         |       -- ReplicationValve
                                         |       -- JvmRouteBinderValve
                                         |
                                         -- LifecycleListener
                                         |
                                         -- ClusterListener
                                         |      \
                                         |       -- ClusterSessionListener
                                         |
                                         -- Deployer
                                                \
                                                 -- FarmWarDeployer
```

### 这个怎么运作

为了便于理解群集的工作原理，我们将带您完成一系列场景。在这种情况下，我们仅计划使用两个tomcat实例`TomcatA`和`TomcatB`。我们将介绍以下事件序列：

1. `TomcatA` 启动
2. `TomcatB` 启动（等待TomcatA启动完成）
3. `TomcatA`收到请求后，将`S1`创建一个会话。
4. `TomcatA` 崩溃
5. `TomcatB` 收到会话请求 `S1`
6. `TomcatA` 启动
7. `TomcatA`收到请求，则在会话（`S1`）上调用了invalidate
8. `TomcatB`收到一个新会话的请求（`S2`）
9. `TomcatA`会话`S2`由于不活动而过期。

好的，既然我们有一个很好的序列，我们将带您准确了解会话复制代码中发生的一切

1. `TomcatA` 启动

   Tomcat使用标准启动顺序启动。创建主机对象后，将与一个群集对象关联。解析上下文时，如果web.xml文件中有可分发元素，则Tomcat会要求Cluster类（在这种情况下`SimpleTcpCluster`）为复制的上下文创建管理器。因此，启用群集后，web.xml中的可分发集将`DeltaManager`为该上下文创建Tomcat 而不是`StandardManager`。群集类将启动成员资格服务（多播）和复制服务（tcp单播）。本文档中进一步介绍了体系结构。

2. `TomcatB` 启动

   TomcatB启动时，它遵循与TomcatA相同的顺序，但有一个例外。集群已启动，并将建立成员资格（TomcatA，TomcatB）。TomcatB现在将向集群中已经存在的服务器（在本例中为TomcatA）请求会话状态。TomcatA响应该请求，并TomcatB开始监听HTTP请求之前，国家已经从TomcatA转移到TomcatB。如果TomcatA没有响应，则TomcatB将在60秒后超时，发出日志条目，然后继续启动。对于在其web.xml中可分发的每个Web应用程序，将转移会话状态。（注意：要有效地使用会话复制，应将所有tomcat实例配置为相同。）

3. `TomcatA`收到请求后，将`S1`创建一个会话。

   进入TomcatA的请求的处理方式与不进行会话复制的方式完全相同，直到请求完成为止。 `ReplicationValve`在响应返回给用户之前将拦截请求。此时，它发现该会话已被修改，并且使用TCP将会话复制到TomcatB。序列化数据移交给操作系统的TCP逻辑后，请求将通过阀门管道返回给用户。对于每个请求，整个会话都将被复制，这允许修改会话中属性的代码无需调用setAttribute或removeAttribute被复制。useDirtyFlag配置参数可用于优化会话被复制的次数。

4. `TomcatA` 崩溃

   当TomcatA崩溃时，TomcatB会收到有关TomcatA退出集群的通知。TomcatB从其成员资格列表中删除了TomcatA，并且TomcatB中发生的任何更改都不会再通知TomcatA。负载平衡器会将请求从TomcatA重定向到TomcatB，并且所有会话均为当前会话。

5. `TomcatB` 收到会话请求 `S1`

   没什么令人兴奋的，TomcatB会像处理其他任何请求一样处理该请求。

6. `TomcatA` 启动

   在启动时，在TomcatA开始接受新请求并使其可用之前，将遵循上述1）2）所述的启动顺序。它将加入集群，请与TomcatB联系以获取所有会话的当前状态。并且一旦收到会话状态，它将完成加载并打开其HTTP / mod_jk端口。因此，直到收到来自TomcatB的会话状态，才会向TomcatA发送请求。

7. `TomcatA`收到请求，则在会话（`S1`）上调用了invalidate

   无效呼叫被截获，并且会话与无效会话排队。请求完成后，它不会发送已更改的会话，而是会向TomcatB发送“过期”消息，并且TomcatB也会使该会话无效。

8. `TomcatB`收到一个新会话的请求（`S2`）

   与步骤3中相同的情况）

9. ```
   TomcatA
   ```

   会话

   ```
   S2
   ```

   由于不活动而过期。

   无效调用的拦截方式与会话使用户无效时的拦截方式相同，并且会话与无效会话一起排队。此时，在另一个请求通过系统并检查无效队列之前，无效会话将不会被复制。

hu！:)

**成员身份** 群集成员身份是使用非常简单的多播ping建立的。每个Tomcat实例会定期发送组播平，ping消息实例将广播其IP和TCP监听端口复制。如果实例在给定的时间范围内未收到此类ping，则该成员被视为已死。很简单，很有效！当然，您需要在系统上启用多播。

**TCP复制** 一旦收到多播ping，该成员将被添加到群集。在下一个复制请求时，发送实例将使用主机和端口信息并建立一个TCP套接字。使用此套接字，它发送序列化的数据。我选择TCP套接字的原因是因为它具有内置的流控制和保证的交付。所以我知道，当我发送一些数据时，它将到达那里:)

**分布式锁定和使用框架的页面** Tomcat不会使会话实例在集群中保持同步。这样的逻辑的实现将花费大量的开销并且会引起各种问题。如果您的客户端使用多个请求同时访问同一会话，则最后一个请求将覆盖群集中的其他会话。

### 使用JMX监视集群

使用群集时，监视是一个非常重要的问题。一些集群对象是JMX MBean

将以下参数添加到启动脚本中：

```bash
set CATALINA_OPTS=\
-Dcom.sun.management.jmxremote \
-Dcom.sun.management.jmxremote.port=%my.jmx.port% \
-Dcom.sun.management.jmxremote.ssl=false \
-Dcom.sun.management.jmxremote.authenticate=false
```

群集Mbean列表

| 名称                | 描述                                                         | MBean ObjectName-引擎                                        | MBean ObjectName-主机                                        |
| :------------------ | :----------------------------------------------------------- | :----------------------------------------------------------- | :----------------------------------------------------------- |
| 簇                  | 完整的集群元素                                               | `type=Cluster`                                               | `type=Cluster,host=${HOST}`                                  |
| DeltaManager        | 该管理器控制会话并处理会话复制                               | `type=Manager,context=${APP.CONTEXT.PATH}, host=${HOST}`     | `type=Manager,context=${APP.CONTEXT.PATH}, host=${HOST}`     |
| FarmWarDeployer     | 管理将应用程序部署到集群中所有节点的过程                     | 不支持                                                       | `type=Cluster, host=${HOST}, component=deployer`             |
| 会员                | 表示集群中的节点                                             | 类型=集群，组件=成员，名称= $ {NODE_NAME}                    | `type=Cluster, host=${HOST}, component=member, name=${NODE_NAME}` |
| 复制阀              | 该阀控制到备份节点的复制                                     | `type=Valve,name=ReplicationValve`                           | `type=Valve,name=ReplicationValve,host=${HOST}`              |
| JvmRouteBinderValve | 这是一个群集回退阀，用于将会话ID更改为当前的tomcat jvmroute。 | `type=Valve,name=JvmRouteBinderValve, context=${APP.CONTEXT.PATH}` | `type=Valve,name=JvmRouteBinderValve,host=${HOST}, context=${APP.CONTEXT.PATH}` |