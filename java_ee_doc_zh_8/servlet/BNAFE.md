# 1.什么是Servlet？

Servlet是Java编程语言类，用于扩展服务器的功能，该服务器承载通过请求-响应编程模型访问的应用程序。尽管Servlet可以响应任何类型的请求，但它们通常用于扩展Web服务器托管的应用程序。对于此类应用程序，Java Servlet技术定义了HTTP特定的Servlet类。

在`javax.servlet`与`javax.servlet.http`包编写servlet提供的接口和类。所有servlet必须实现`Servlet`定义生命周期方法的 接口。实施通用服务时，可以使用或扩展`GenericServlet`Java Servlet API随附的类。本`HttpServlet`类提供的方法，如`doGet`和`doPost`，用于处理特定的HTTP服务。