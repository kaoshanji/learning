# 22.9 主题

### 22.9.1主题概述

您可以应用Spring Web MVC框架主题来设置应用程序的整体外观，从而增强用户体验。主题是静态资源的集合，通常是样式表和图像，它们会影响应用程序的视觉样式。

### 22.9.2定义主题

要在Web应用程序中使用主题，必须设置`org.springframework.ui.context.ThemeSource`接口的实现 。该`WebApplicationContext` 接口扩展`ThemeSource`，但其代表职责的专用实现。默认情况下，委托将是一个`org.springframework.ui.context.support.ResourceBundleThemeSource`从类路径的根目录加载属性文件的 实现。要使用自定义`ThemeSource` 实现或配置其基本名称前缀`ResourceBundleThemeSource`，可以在应用程序上下文中使用保留名称注册bean `themeSource`。Web应用程序上下文自动检测具有该名称的bean并使用它。

使用时`ResourceBundleThemeSource`，主题在简单的属性文件中定义。属性文件列出构成主题的资源。这是一个例子：

```properties
styleSheet=/themes/cool/style.css
background=/themes/cool/img/coolBg.jpg
```

属性的键是从视图代码引用主题元素的名称。对于JSP，通常使用`spring:theme`自定义标记执行此操作，该标记与`spring:message`标记非常相似。以下JSP片段使用上一示例中定义的主题来自定义外观：

```html
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<html>
    <head>
        <link rel="stylesheet" href="<spring:theme code='styleSheet'/>" type="text/css"/>
    </head>
    <body style="background=<spring:theme code='background'/>">
        ...
    </body>
</html>
```

默认情况下，`ResourceBundleThemeSource`使用空的基本名称前缀。因此，属性文件从类路径的根加载。因此，您可以将 `cool.properties`主题定义放在类路径根目录的目录中，例如，在`/WEB-INF/classes`。在`ResourceBundleThemeSource`使用标准的Java资源包加载机制，允许主题的国际化。例如，我们`/WEB-INF/classes/cool_nl.properties`可以引用一个带有荷兰文字的特殊背景图像。

### 22.9.3主题解析器

定义主题后，如上一节所述，您可以决定使用哪个主题。该 `DispatcherServlet`会寻找一个叫豆`themeResolver`，以找出 `ThemeResolver`使用实施。主题解析器的工作方式与a非常相似 `LocaleResolver`。它检测用于特定请求的主题，还可以更改请求的主题。Spring提供以下主题解析器：



**表22.5。ThemeResolver实现**

| 类                     | 描述                                                         |
| ---------------------- | ------------------------------------------------------------ |
| `FixedThemeResolver`   | 选择使用`defaultThemeName`属性设置的固定主题。               |
| `SessionThemeResolver` | 主题在用户的HTTP会话中维护。它只需要为每个会话设置一次，但不会在会话之间保留。 |
| `CookieThemeResolver`  | 所选主题存储在客户端的cookie中。                             |

Spring还提供了一个`ThemeChangeInterceptor`允许使用简单的请求参数对每个请求进行主题更改的方法。