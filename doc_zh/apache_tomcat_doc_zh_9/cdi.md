# 35.CDI和JAX-RS支持

### 介绍

CDI和JAX-RS是许多其他API和库的依赖项。本指南说明了如何使用Tomcat源代码中提供的两个可选模块在Tomcat中添加对它们的支持。

### CDI 2支持

`modules/owb`可选模块 提供了CDI 2支持。它打包了Apache OpenWebBeans项目，并允许将CDI 2支持添加到Tomcat容器。该模块的构建过程使用Apache Maven，由于它是使用许多公共可用的JAR构建的，因此不能作为二进制捆绑包使用。

建立CDI支持的过程如下。

```bash
cd $TOMCAT_SRC/modules/owb
mvn clean && mvn package
```

然后将生成的JAR `target/tomcat-owb-x.y.z.jar``lib`
`server.xml``Server`

```xml
<Listener className="org.apache.webbeans.web.tomcat.OpenWebBeansListener" optional="true" startWithoutBeansXml="false" />
```

如果CDI容器加载失败，则侦听器将产生非致命错误。
`context.xml``Server`

```xml
<Listener className="org.apache.webbeans.web.tomcat.OpenWebBeansContextLifecycleListener" />
```



### JAX-RS支持

`modules/cxf`可选模块 提供了JAX-RS支持。它封装了Apache CXF项目，并允许添加JAX-RS个别的webapps支持。该模块的构建过程使用Apache Maven，由于它是使用许多公共可用的JAR构建的，因此不能作为二进制捆绑包使用。该支持取决于CDI 2支持，该支持以前应该已经在容器或Webapp级别上安装。

建立JAX-RS支持的过程如下。

```bash
cd $TOMCAT_SRC/modules/cxf
mvn clean && mvn package
target/tomcat-cxf-x.y.z.jar``/WEB-INF/lib
```



如果在容器级别提供了CDI 2支持，则还可以将JAR放置在Tomcat `lib`文件夹中，但是在这种情况下，必须根据需要在每个Web应用程序中分别添加CXF Servlet声明（通常由该Web片段加载在JAR中）。应该使用的CXF Servlet类是`org.apache.cxf.cdi.CXFCdiServlet`并且应该映射到可以使用 JAX-RS资源的所需根路径。

### Eclipse Microprofile支持

可以使用ASF工件使用CDI 2扩展来实现Eclipse Microprofile规范。一旦安装了CDI 2和JAX-RS支持，它们将可被单个Web应用程序使用。

以下实现`org.apache.tomee.microprofile.TomEEMicroProfileListener`可用作Maven工件（参考 ：），必须将其添加到webapp `/WEB-INF/lib` 文件夹中：

- **配置**：Maven工件： `org.apache.geronimo.config:geronimo-config` CDI扩展类： `org.apache.geronimo.config.cdi.ConfigExtension`
- **容错**：Maven工件： `org.apache.geronimo.safeguard:safeguard-parent` CDI扩展类： `org.apache.safeguard.impl.cdi.SafeguardExtension`
- **运行状况**：Maven工件： `org.apache.geronimo:geronimo-health` CDI扩展类： `org.apache.geronimo.microprofile.impl.health.cdi.GeronimoHealthExtension`
- **指标**：Maven工件： `org.apache.geronimo:geronimo-metrics` CDI扩展类： `org.apache.geronimo.microprofile.metrics.cdi.MetricsExtension`
- **OpenTracing**：Maven工件： `org.apache.geronimo:geronimo-opentracing` CDI扩展类： `org.apache.geronimo.microprofile.opentracing.microprofile.cdi.OpenTracingExtension`
- **OpenAPI**：Maven工件： `org.apache.geronimo:geronimo-openapi` CDI扩展类： `org.apache.geronimo.microprofile.openapi.cdi.GeronimoOpenAPIExtension`
- **其余客户端**：Maven工件： `org.apache.cxf:cxf-rt-rs-mp-client` CDI扩展类： `org.apache.cxf.microprofile.client.cdi.RestClientExtension`
- **JSON Web令牌**：注意：仅供参考，在Apache TomEE外部不可用；Maven工件： `org.apache.tomee:mp-jwt` CDI扩展类： `org.apache.tomee.microprofile.jwt.cdi.MPJWTCDIExtension`