# 8.访问Web上下文

Web组件在其中执行的上下文是实现`ServletContext`接口的对象。您使用`getServletContext`方法检索Web上下文。Web上下文提供了访问方法

- 初始化参数
- 与Web上下文关联的资源
- 对象值属性
- 记录功能

计数器的访问方法是同步的，以防止同时运行的servlet进行不兼容的操作。过滤器使用上下文的`getAttribute`方法检索计数器对象。计数器的增量值记录在日志中。