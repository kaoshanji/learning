# 22.12 安全

在[春季安全](https://projects.spring.io/spring-security/)项目提供的功能，以防止恶意攻击Web应用程序。查看[“CSRF保护”](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#csrf)， [“安全响应标头”](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#headers)以及 [“Spring MVC集成”](https://docs.spring.io/spring-security/site/docs/current/reference/htmlsingle/#mvc)部分中的参考文档 。请注意，并非所有功能都必须使用Spring Security来保护应用程序。例如，可以通过添加`CsrfFilter`和 `CsrfRequestDataValueProcessor`配置来添加CSRF保护。有关 示例，请参阅 [Spring MVC Showcase](https://github.com/spring-projects/spring-mvc-showcase/commit/361adc124c05a8187b84f25e8a57550bb7d9f8e4)。

另一种选择是使用专用于Web安全的框架。 [HDIV](https://hdiv.org/)就是这样一个框架，并与Spring MVC集成。