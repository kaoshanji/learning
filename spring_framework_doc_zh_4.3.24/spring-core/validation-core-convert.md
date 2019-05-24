# 9.5 类型转换

Spring 3引入了一个`core.convert`提供通用类型转换系统的包。系统定义了一个SPI来实现类型转换逻辑，以及一个在运行时执行类型转换的API。在Spring容器中，此系统可用作PropertyEditors的替代方法，以将外部化的bean属性值字符串转换为必需的属性类型。公共API也可以在您的应用程序中需要进行类型转换的任何地方使用。

### 9.5.1转换器SPI

用于实现类型转换逻辑的SPI简单且强类型化：

```java
package org.springframework.core.convert.converter;

public interface Converter<S, T> {

    T convert(S source);
}
```

要创建自己的转换器，只需实现上面的接口即可。参数`S` 化为您要转换的类型，以及`T`您要转换为的类型。如果`S`需要将集合或阵列转换为阵列或集合，则也可以透明地应用这种转换器`T`，前提是已经注册了委托阵列/集合转换器（`DefaultConversionService`默认情况下）。

对于每次调用`convert(S)`，source参数保证为NOT null。如果转换失败，您的Converter可能会抛出任何未经检查的异常; 具体而言， `IllegalArgumentException`应该抛出一个报告无效的源值。注意确保您的`Converter`实现是线程安全的。

`core.convert.support`为方便起见，在包中提供了几种转换器实现。这些包括从字符串到数字的转换器和其他常见类型。考虑`StringToInteger`作为典型`Converter`实现的示例：

```java
package org.springframework.core.convert.support;

final class StringToInteger implements Converter<String, Integer> {

    public Integer convert(String source) {
        return Integer.valueOf(source);
    }
}
```

### 9.5.2 ConverterFactory

当您需要集中整个类层次结构的转换逻辑时，例如，从String转换为java.lang.Enum对象时，请实现 `ConverterFactory`：

```java
package org.springframework.core.convert.converter;

public interface ConverterFactory<S, R> {

    <T extends R> Converter<S, T> getConverter(Class<T> targetType);
}
```

参数化S为要转换的类型，R为定义可转换为的类*范围*的基本类型。然后实现getConverter（Class <T>），其中T是R的子类。

以`StringToEnum`ConverterFactory为例：

```java
package org.springframework.core.convert.support;

final class StringToEnumConverterFactory implements ConverterFactory<String, Enum> {

    public <T extends Enum> Converter<String, T> getConverter(Class<T> targetType) {
        return new StringToEnumConverter(targetType);
    }

    private final class StringToEnumConverter<T extends Enum> implements Converter<String, T> {

        private Class<T> enumType;

        public StringToEnumConverter(Class<T> enumType) {
            this.enumType = enumType;
        }

        public T convert(String source) {
            return (T) Enum.valueOf(this.enumType, source.trim());
        }
    }
}
```

### 9.5.3 GenericConverter

当您需要复杂的Converter实现时，请考虑GenericConverter接口。通过更灵活但不太强类型的签名，GenericConverter支持在多个源类型和目标类型之间进行转换。此外，GenericConverter提供了在实现转换逻辑时可以使用的源和目标字段上下文。这样的上下文允许类型转换由字段注释或在字段签名上声明的通用信息驱动。

```java
package org.springframework.core.convert.converter;

public interface GenericConverter {

    public Set<ConvertiblePair> getConvertibleTypes();

    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);
}
```

要实现GenericConverter，请让getConvertibleTypes（）返回支持的源→目标类型对。然后实现convert（Object，TypeDescriptor，TypeDescriptor）来实现转换逻辑。源TypeDescriptor提供对包含要转换的值的源字段的访问。目标TypeDescriptor提供对将设置转换值的目标字段的访问。

GenericConverter的一个很好的例子是在Java Array和Collection之间进行转换的转换器。这样的ArrayToCollectionConverter会对声明目标Collection类型的字段进行内省，以解析Collection的元素类型。这允许在目标字段上设置Collection之前，将源数组中的每个元素转换为Collection元素类型。

因为GenericConverter是一个更复杂的SPI接口，所以只在需要时使用它。喜欢Converter或ConverterFactory以满足基本的类型转换需求。

#### ConditionalGenericConverter

有时您只希望在`Converter`特定条件成立时执行。例如，`Converter`如果目标字段上存在特定注释，则可能只想执行a 。或者，`Converter`如果`static valueOf`在目标类上定义了特定方法（如方法），则可能只想执行a 。 `ConditionalGenericConverter`是`GenericConverter`和 `ConditionalConverter`接口的联合，允许您定义这样的自定义匹配条件：

```java
public interface ConditionalConverter {

    boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType);
}

public interface ConditionalGenericConverter extends GenericConverter, ConditionalConverter {
}
```

一个很好的例子`ConditionalGenericConverter`是EntityConverter，它在持久实体标识符和实体引用之间进行转换。如果目标实体类型声明静态查找器方法，则这样的EntityConverter可能仅匹配 `findAccount(Long)`。你会在执行中执行这样的finder方法检查 `matches(TypeDescriptor, TypeDescriptor)`。

### 9.5.4 ConversionService API

ConversionService定义了一个统一的API，用于在运行时执行类型转换逻辑。转换器通常在此Facade接口后面执行：

```java
package org.springframework.core.convert;

public interface ConversionService {

    boolean canConvert(Class<?> sourceType, Class<?> targetType);

    <T> T convert(Object source, Class<T> targetType);

    boolean canConvert(TypeDescriptor sourceType, TypeDescriptor targetType);

    Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType);

}
```

大多数ConversionService实现也实现`ConverterRegistry`，它提供用于注册转换器的SPI。在内部，ConversionService实现委托其注册的转换器执行类型转换逻辑。

`core.convert.support` 包中提供了强大的ConversionService实现。`GenericConversionService`是适用于大多数环境的通用实现。`ConversionServiceFactory`提供了一个方便的工厂来创建常见的ConversionService配置。

### 9.5.5配置ConversionService

ConversionService是一个无状态对象，旨在在应用程序启动时实例化，然后在多个线程之间共享。在Spring应用程序中，通常为每个Spring容器（或ApplicationContext）配置一个ConversionService实例。那个ConversionService将被Spring选中，然后在框架需要执行类型转换时使用。您也可以将此ConversionService注入任何bean并直接调用它。

如果没有向Spring注册ConversionService，则使用基于PropertyEditor的原始系统。

要使用Spring注册默认的ConversionService，请使用id添加以下bean定义`conversionService`：

```xml
<bean id="conversionService"
    class="org.springframework.context.support.ConversionServiceFactoryBean"/>
```

默认的ConversionService可以在字符串，数字，枚举，集合，映射和其他常见类型之间进行转换。要使用您自己的自定义转换器补充或覆盖默认转换器，请设置该`converters`属性。属性值可以实现Converter，ConverterFactory或GenericConverter接口。

```xml
<bean id="conversionService"
        class="org.springframework.context.support.ConversionServiceFactoryBean">
    <property name="converters">
        <set>
            <bean class="example.MyCustomConverter"/>
        </set>
    </property>
</bean>
```

在Spring MVC应用程序中使用ConversionService也很常见。

### 9.5.6以编程方式使用ConversionService

要以编程方式使用ConversionService实例，只需像对任何其他bean一样注入对它的引用：

```java
@Service
public class MyService {

    @Autowired
    public MyService(ConversionService conversionService) {
        this.conversionService = conversionService;
    }

    public void doIt() {
        this.conversionService.convert(...)
    }
}
```

对于大多数用例，可以使用`convert`指定*targetType*的方法，但它不适用于更复杂的类型，例如参数化元素的集合。如果你想转换`List`的`Integer`到`List`的`String`程序，例如，你需要提供的源和目标类型的正式定义。

幸运的是，`TypeDescriptor`提供了各种选项来简化：

```java
DefaultConversionService cs = new DefaultConversionService();

List<Integer> input = ....
cs.convert(input,
    TypeDescriptor.forObject(input), // List<Integer> type descriptor
    TypeDescriptor.collection(List.class, TypeDescriptor.valueOf(String.class)));
```

请注意，`DefaultConversionService`自动寄存器转换器适用于大多数环境。这包括收集器，标转换器，也基本`Object`到`String`转换器。可以`ConverterRegistry`使用类中的*静态* `addDefaultConverters` 方法向任何注册相同的转换器`DefaultConversionService`。

值类型转换器将被重新用于数组和集合，所以没有必要创建一个特定的转换器从转换`Collection`的`S`到 `Collection`的`T`，假设标准收集处理是适当的。