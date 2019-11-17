# 26.高级IO

### 介绍

**重要说明：这些功能的使用要求使用HTTP连接器。AJP连接器不支持它们。**

### 异步写入

当使用HTTP连接器（基于APR或NIO / NIO2）时，Tomcat支持使用sendfile发送大型静态文件。一旦系统负载增加，这些写入将以最有效的方式异步执行。除了使用阻塞写入来发送较大响应外，还可以将内容写入静态文件，然后使用sendfile代码将其写入。缓存阀可以利用此优势将响应数据缓存在文件中，而不是将其存储在内存中。如果request属性`org.apache.tomcat.sendfile.support` 设置为，则可以使用Sendfile支持`Boolean.TRUE`。

任何servlet都可以通过设置适当的请求属性来指示Tomcat执行sendfile调用。还必须正确设置响应的内容长度。使用sendfile时，最好确保请求或响应都没有被包装，因为由于响应主体稍后将由连接器本身发送，因此无法对其进行过滤。除了设置3个必需的请求属性外，该servlet不应发送任何响应数据，但可以使用任何会导致修改响应标头的方法（例如设置cookie）。

- `org.apache.tomcat.sendfile.filename`：将以字符串形式发送的文件的规范文件名
- `org.apache.tomcat.sendfile.start`：开始偏移为长整型
- `org.apache.tomcat.sendfile.end`：结束偏移为长整型

除了设置这些参数之外，还必须设置content-length标头。Tomcat不会为您执行此操作，因为您可能已经将数据写入了输出流。

请注意，使用sendfile将禁用Tomcat可能会对响应执行的任何压缩。