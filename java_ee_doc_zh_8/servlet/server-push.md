# 15.服务器推送

服务器推送是服务器在客户端请求之前预测客户端将需要什么的能力。它允许服务器在浏览器要求将资源放入缓存之前，预先填充浏览器的缓存。

服务器推送是显示在Servlet API中的HTTP / 2改进中最明显的。HTTP / 2中的所有新功能（包括服务器推送）均旨在提高Web浏览体验的性能。

服务器推送是由于服务器知道初始请求会附带哪些附加资产（例如图像，样式表和脚本），从而为提高浏览器性能做出了贡献。例如，服务器可能知道，每当浏览器请求`index.html`，它将此后不久请求`header.gif`，`footer.gif`和`style.css`。服务器可以抢先开始发送这些资产的字节和的字节`index.html`。

使用服务器推动，获得到的参考`PushBuilder`从一个`HttpServletRequest`，编辑助洗剂如需要的话，然后调用`push()`。有关类 和方法， 请参见 [javadoc](https://javaee.github.io/javaee-spec/)。`javax.servlet.http.PushBuilder``javax.servlet.http.HttpServletRequest.newPushBuilder()`

要查看此功能的GlassFish示例代码，请参见 https://github.com/javaee/glassfish-samples/tree/master/ws/javaee8。