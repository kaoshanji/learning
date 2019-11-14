# 59. Naming spans

## 59.命名范围

选择一个跨度名称不是一件容易的事。跨度名称应描述一个操作名称。该名称应为低基数，因此不应包含标识符。

由于正在进行很多检测，因此一些跨度名称是人为的：

- `controller-method-name` 由控制器接收的方法名称为 `controllerMethodName`
- `async`用于使用包装`Callable`和`Runnable`接口完成的异步操作。
- 带注解的方法`@Scheduled`返回类的简单名称。

幸运的是，对于异步处理，您可以提供显式命名。

## 59.1 `@SpanName`注释

您可以使用`@SpanName`批注显式命名跨度，如以下示例所示：

```java
	@SpanName("calculateTax")
	class TaxCountingRunnable implements Runnable {

		@Override
		public void run() {
			// perform logic
		}

	}

}
```

在这种情况下，按以下方式进行处理时，跨度将被命名为`calculateTax`：

```java
Runnable runnable = new TraceRunnable(this.tracing, spanNamer,
		new TaxCountingRunnable());
Future<?> future = executorService.submit(runnable);
// ... some additional logic ...
future.get();
```

## 59.2 `toString()`方法

为`Runnable`或创建单独的类非常少见`Callable`。通常，创建一个匿名类的实例。您不能注释此类。为了克服该限制，如果没有`@SpanName`注释，则检查类是否具有该`toString()`方法的自定义实现。

运行这样的代码将导致创建一个名为的跨度`calculateTax`，如以下示例所示：

```java
Runnable runnable = new TraceRunnable(this.tracing, spanNamer, new Runnable() {
	@Override
	public void run() {
		// perform logic
	}

	@Override
	public String toString() {
		return "calculateTax";
	}
});
Future<?> future = executorService.submit(runnable);
// ... some additional logic ...
future.get();
```