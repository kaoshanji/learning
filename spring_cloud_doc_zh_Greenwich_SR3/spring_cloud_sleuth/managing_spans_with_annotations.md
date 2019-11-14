# 60. Managing Spans with Annotations

## 60.使用注释管理跨度

您可以使用各种注释来管理跨度。

## 60.1基础

使用注释管理跨区有很多充分的理由，包括：

- 与API无关的方法可以与跨度进行协作。使用注释使用户可以添加到跨度，而跨度api不依赖库。这样，Sleuth可以更改其核心API，以减少对用户代码的影响。
- 基本跨度操作减少了表面积。如果没有此功能，则必须使用span api，该api的生命周期命令可能无法正确使用。通过仅公开作用域，标记和日志功能，您可以进行协作而不会意外中断跨度生命周期。
- 与运行时生成的代码协作。使用诸如Spring Data和Feign之类的库，可以在运行时生成接口的实现。因此，对象的跨度包裹是乏味的。现在，您可以在接口和这些接口的参数上提供注释。

## 60.2创建新的跨度

如果您不想手动创建本地范围，则可以使用`@NewSpan`注释。另外，我们提供`@SpanTag`注释以自动方式添加标签。

现在我们可以考虑一些用法示例。

```java
@NewSpan
void testMethod();
```

在不带任何参数的情况下对方法进行注释会导致创建一个新的跨度，该跨度的名称等于带注释的方法名称。

```java
@NewSpan("customNameOnTestMethod4")
void testMethod4();
```

如果您在批注中提供值（直接或通过设置`name`参数），则创建的跨度将提供的值作为名称。

```java
// method declaration
@NewSpan(name = "customNameOnTestMethod5")
void testMethod5(@SpanTag("testTag") String param);

// and method execution
this.testBean.testMethod5("test");
```

您可以同时使用名称和标签。让我们专注于后者。在这种情况下，带注释的方法的参数运行时值的值将成为标签的值。在我们的示例中，标记键是`testTag`，标记值是`test`。

```java
@NewSpan(name = "customNameOnTestMethod3")
@Override
public void testMethod3() {
}
```

您可以将`@NewSpan`注释放在类和接口上。如果您重写接口的方法并为`@NewSpan`注释提供不同的值，则最具体的一个将获胜（在这种情况下`customNameOnTestMethod3`已设置）。

## 60.3连续跨度

如果要将标记和注释添加到现有范围，则可以使用`@ContinueSpan`注释，如以下示例所示：

```java
// method declaration
@ContinueSpan(log = "testMethod11")
void testMethod11(@SpanTag("testTag11") String param);

// method execution
this.testBean.testMethod11("test");
this.testBean.testMethod13();
```

（请注意，与`@NewSpan`注释相反，您还可以使用`log`参数添加日志。）

这样，跨度将继续，并且：

- 命名为`testMethod11.before`和的日志条目`testMethod11.after`已创建。
- 如果引发异常，`testMethod11.afterFailure`还将创建一个名为的日志条目。
- 将创建一个键为`testTag11`和值为的标签`test`。

## 60.4高级标签设置

有3种不同的方法可以将标签添加到跨度。它们全部由`SpanTag`注释控制。优先级如下：

1. 尝试使用`TagValueResolver`类型和提供的名称的Bean 。
2. 如果未提供Bean名称，请尝试评估表达式。我们搜索一个`TagValueExpressionResolver`豆子。默认实现使用SPEL表达式解析。 **重要事项**您只能从SPEL表达式中引用属性。由于安全限制，不允许执行方法。
3. 如果找不到任何要求`toString()`值的表达式，请返回参数的值。

### 60.4.1定制提取器

用于以下方法的标记的值由`TagValueResolver`接口的实现计算。它的类名必须作为`resolver`属性的值传递。

考虑以下带注释的方法：

```java
@NewSpan
public void getAnnotationForTagValueResolver(
		@SpanTag(key = "test", resolver = TagValueResolver.class) String test) {
}
```

现在进一步考虑以下`TagValueResolver`bean实现：

```java
@Bean(name = "myCustomTagValueResolver")
public TagValueResolver tagValueResolver() {
	return parameter -> "Value from myCustomTagValueResolver";
}
```

前面的两个示例导致将标签值设置为`Value from myCustomTagValueResolver`。

### 60.4.2解析一个值的表达式

考虑以下带注释的方法：

```java
@NewSpan
public void getAnnotationForTagValueExpression(
		@SpanTag(key = "test", expression = "'hello' + ' characters'") String test) {
}
```

没有定制的实现实现`TagValueExpressionResolver`会导致评估SPEL表达式，并且`4 characters`在跨度上设置了值为的标签。如果要使用其他表达式解析机制，则可以创建自己的bean实现。

### 60.4.3使用`toString()`方法

考虑以下带注释的方法：

```java
@NewSpan
public void getAnnotationForArgumentToString(@SpanTag("test") Long param) {
}
```

使用的值运行前面的方法`15`会导致设置String值为的标签`"15"`。