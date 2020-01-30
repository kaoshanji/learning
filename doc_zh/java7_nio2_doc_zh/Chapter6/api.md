# Watch API

java.nio.file.WatchService接口是此API的起点。 

它有多个不同文件系统和操作系统的实现。 

您可以将此接口与三个类来开发具有文件系统监视功能的系统。 

这些类的概述以下项目符号：

-   Watchable

如果对象表示类的实例，则该对象是“可观察的”实现java.nio.file.Watchable接口。 

就我们而言，这是NIO 2最重要的类，著名的Path类.

-   Event types

这是我们有兴趣监视的事件列表。 

事件触发仅当在注册调用中指定了通知时才发出通知。 

支持的标准事件由java.nio.file.StandardWatchEventKinds类表示，并且包括创建，删除和修改。 

此类实现`WatchEvent.Kind <T>`接口。

-   Event modifier

这说明了如何在WatchService中注册Watchable。

截至撰写本文时，NIO.2尚未定义任何标准修饰符

-   Watcher

观看者观看手表！ 

在我们的示例中，观察者是 WatchService，它监视文件系统的更改（文件系统是FileSystem实例）。

 如您所见，WatchService将通过以下方式创建FileSystem类。 
 
它将在后台静默观看，注册路径

----