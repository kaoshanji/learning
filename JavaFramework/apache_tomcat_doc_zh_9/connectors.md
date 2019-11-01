# 21.连接器

### 介绍

选择要与Tomcat一起使用的连接器可能很困难。此页面将列出此Tomcat版本支持的连接器，并有望帮助您根据需要做出正确的选择。

### HTTP

缺省情况下，HTTP连接器是使用Tomcat设置的，可以使用。该连接器具有最低的延迟和最佳的整体性能。

对于群集， 必须安装**支持Web会话粘性**的HTTP负载平衡器**，**以将流量定向到Tomcat服务器。Tomcat支持mod_proxy（在Apache HTTP Server 2.x上，默认情况下包含在Apache HTTP Server 2.2中）作为负载平衡器。应当注意，HTTP代理的性能通常低于AJP的性能，因此AJP群集通常是更可取的。

### AJP

使用单个服务器时，即使在大部分Web应用程序由静态文件构成的情况下，在大多数情况下，使用Tomcat实例前面的本机Web服务器的性能也比具有默认HTTP连接器的独立Tomcat的性能明显差。 。如果出于任何原因需要与本机Web服务器集成，则AJP连接器将提供比代理HTTP更快的性能。从Tomcat的角度来看，AJP集群是最有效的。它在功能上等效于HTTP群集。

此Tomcat发行版支持的本机连接器是：

- JK 1.2.x与任何受支持的服务器
- Apache HTTP Server 2.x上的mod_proxy（默认情况下包含在Apache HTTP Server 2.2中），并且启用了AJP

**支持AJP的其他本机连接器可能会起作用，但不再受支持。**