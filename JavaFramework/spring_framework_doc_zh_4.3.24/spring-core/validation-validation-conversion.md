# 9.3 转换错误信息

我们已经讨论了数据绑定和验证。输出与验证错误相对应的消息是我们需要讨论的最后一件事。在我们上面显示的示例中，我们拒绝了`name`该`age`字段。如果我们要使用a输出错误消息`MessageSource`，我们将使用我们在拒绝字段时给出的错误代码（在这种情况下为'name'和'age'）。当您（直接或间接地使用例如`ValidationUtils`类）`rejectValue`或接口中的其他`reject`方法之一调用时`Errors`，底层实现不仅会注册您传入的代码，还会注册许多其他错误代码。它注册的错误代码由`MessageCodesResolver`使用的错误代码决定。默认情况下`DefaultMessageCodesResolver`使用，例如，不仅使用您提供的代码注册消息，还包含您传递给reject方法的字段名称的消息。因此，如果您拒绝使用字段 `rejectValue("age", "too.darn.old")`，除了`too.darn.old`代码之外，Spring还会注册`too.darn.old.age`并且`too.darn.old.age.int`（因此第一个将包含字段名称，第二个将包括字段的类型）; 这样做是为了方便开发人员定位错误消息等。

关于更多信息`MessageCodesResolver`以及默认的策略可以在网上的Javadoc中找到 [`MessageCodesResolver`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/validation/MessageCodesResolver.html) 并 [`DefaultMessageCodesResolver`](https://docs.spring.io/spring-framework/docs/4.3.24.RELEASE/javadoc-api/org/springframework/validation/DefaultMessageCodesResolver.html)分别。

