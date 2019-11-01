# 3.分享信息

与大多数对象一样，Web组件通常与其他对象一起工作以完成其任务。Web组件可以执行以下操作。

- 使用私有帮助器对象（例如JavaBeans组件）。
- 共享作为公共范围属性的对象。
- 使用数据库。
- 调用其他Web资源。在Java Servlet技术的机制，允许网络组件来调用其他网络资源中描述[调用其他Web资源](BNAGI.md)。



### 使用范围对象

协作Web组件通过作为四个作用域对象的属性维护的对象来共享信息。您可以使用代表作用域的类的`getAttribute`和`setAttribute`方法访问这些属性。[表18-2](https://javaee.github.io/tutorial/servlets003.html#BNAFQ)列出了作用域对象。



**表18-2范围对象**

| **范围对象** | **类**                                  | **可从访问**                                                 |
| ------------ | --------------------------------------- | ------------------------------------------------------------ |
| Web context  | `javax.servlet.ServletContext`          | Web上下文中的Web组件。请参阅[访问Web上下文](https://javaee.github.io/tutorial/servlets008.html#BNAGL)。 |
| Session      | `javax.servlet.http.HttpSession`        | Web组件处理属于该会话的请求。请参阅 [维护客户状态](https://javaee.github.io/tutorial/servlets009.html#BNAGM)。 |
| Request      | 的子类型 `javax.servlet.ServletRequest` | Web组件处理请求。                                            |
| Page         | `javax.servlet.jsp.JspContext`          | 创建对象的JSP页面。                                          |



### 控制对共享资源的并发访问

在多线程服务器中，可以同时访问共享资源。除了作用域对象属性外，共享资源还包括内存数据（例如实例或类变量）和外部对象（例如文件，数据库连接和网络连接）。

并发访问可能在几种情况下出现。

- 多个Web组件访问存储在Web上下文中的对象。
- 多个Web组件访问会话中存储的对象。
- Web组件中的多个线程正在访问实例变量。Web容器通常会创建一个线程来处理每个请求。为了确保Servlet实例一次仅处理一个请求，servlet可以实现该`SingleThreadModel`接口。如果servlet实现此接口，则该servlet的service方法中将不会同时执行两个线程。Web容器可以通过同步对Servlet单个实例的访问或维护Web组件实例池并将每个新请求分配给空闲实例来实现此保证。此接口不能防止由于Web组件访问共享资源（例如静态类变量或外部对象）而导致的同步问题。

当可以同时访问资源时，它们可能会以不一致的方式使用。通过使用“线程”课程中描述的同步技术来控制访问，可以防止这种情况的发生 `http://docs.oracle.com/javase/tutorial/essential/concurrency/`。