# SeekableByteChannel实现随机访问

新的SeekableByteChannel接口通过实现以下概念为RAF提供支持：在渠道上的位置。 

我们可以从一个通道读取或写入一个ByteBuffer，获取或设置当前位置，并将与通道连接的实体截断到指定尺寸。 

下列方法与这些功能相关联（更多详细信息可在以下位置的官方文档中找到：http://download.oracle.com/javase/7/docs/api/index.html）：


-   position()

返回频道的当前位置（非负数）

-   position(long)

将频道的位置设置为指定的多头（非负数）。

将头寸设置为大于当前大小的值是合法的，但确实不会更改实体的大小。

-   truncate(long)

将连接到通道的实体截断到指定的位置长。

-   read(ByteBuffer)

从通道将字节读取到缓冲区。

-   write(ByteBuffer)

将字节从缓冲区写入通道。





