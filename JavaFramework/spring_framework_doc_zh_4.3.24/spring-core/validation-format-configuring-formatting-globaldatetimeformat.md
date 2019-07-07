# 9.7 全局日期和格式

默认情况下，未`@DateTimeFormat`使用该`DateFormat.SHORT`样式转换的字符串将转换未注释的日期和时间字段。如果您愿意，可以通过定义自己的全局格式来更改此设置。

您需要确保Spring不会注册默认格式化程序，而是应该手动注册所有格式化程序。根据您是否使用Joda-Time库，使用`org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar`或 `org.springframework.format.datetime.DateFormatterRegistrar`类。

例如，以下Java配置将注册全局“yyyyMMdd”格式。此示例不依赖于Joda-Time库：

```java
@Configuration
public class AppConfig {

    @Bean
    public FormattingConversionService conversionService() {

        // Use the DefaultFormattingConversionService but do not register defaults
        DefaultFormattingConversionService conversionService = new DefaultFormattingConversionService(false);

        // Ensure @NumberFormat is still supported
        conversionService.addFormatterForFieldAnnotation(new NumberFormatAnnotationFormatterFactory());

        // Register date conversion with a specific global format
        DateFormatterRegistrar registrar = new DateFormatterRegistrar();
        registrar.setFormatter(new DateFormatter("yyyyMMdd"));
        registrar.registerFormatters(conversionService);

        return conversionService;
    }
}
```

如果您更喜欢基于XML的配置，则可以使用 `FormattingConversionServiceFactoryBean`。这是相同的例子，这次使用Joda Time：

```xml
<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="
        http://www.springframework.org/schema/beans
        https://www.springframework.org/schema/beans/spring-beans.xsd>

    <bean id="conversionService" class="org.springframework.format.support.FormattingConversionServiceFactoryBean">
        <property name="registerDefaultFormatters" value="false" />
        <property name="formatters">
            <set>
                <bean class="org.springframework.format.number.NumberFormatAnnotationFormatterFactory" />
            </set>
        </property>
        <property name="formatterRegistrars">
            <set>
                <bean class="org.springframework.format.datetime.joda.JodaTimeFormatterRegistrar">
                    <property name="dateFormatter">
                        <bean class="org.springframework.format.datetime.joda.DateTimeFormatterFactoryBean">
                            <property name="pattern" value="yyyyMMdd"/>
                        </bean>
                    </property>
                </bean>
            </set>
        </property>
    </bean>
</beans>
```

乔达时提供单独的不同类型来表示`date`，`time`和`date-time` 的值。的`dateFormatter`，`timeFormatter`和`dateTimeFormatter`的性质`JodaTimeFormatterRegistrar`，应使用来配置不同的格式为每种类型。它`DateTimeFormatterFactoryBean`提供了一种创建格式化程序的便捷方法。

如果您正在使用Spring MVC，请记住明确配置所使用的转换服务。对于基于Java的，`@Configuration`这意味着扩展 `WebMvcConfigurationSupport`类并重写`mvcConversionService()`方法。对于XML，您应该使用元素的`'conversion-service'`属性 `mvc:annotation-driven`。