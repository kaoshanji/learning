# 17、Spring Bean 和 依赖注入

您可以自由使用任何标准的Spring Framework技术来定义bean及其注入的依赖关系。为简单起见，我们经常发现使用`@ComponentScan`（查找您的bean）和使用`@Autowired`（进行构造函数注入）效果很好。

如果按照上面的建议构造代码（将应用程序类放在根包中），则可以添加`@ComponentScan`而无需任何参数。您的所有应用程序组件（的`@Component`，`@Service`，`@Repository`，`@Controller`等）自动注册为春豆。

以下示例显示了一个`@Service`使用构造函数注入来获取所需`RiskAssessor`Bean的Bean：

```java
package com.example.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class DatabaseAccountService implements AccountService {

	private final RiskAssessor riskAssessor;

	@Autowired
	public DatabaseAccountService(RiskAssessor riskAssessor) {
		this.riskAssessor = riskAssessor;
	}

	// ...

}
```

如果bean具有一个构造函数，则可以省略`@Autowired`，如以下示例所示：

```java
@Service
public class DatabaseAccountService implements AccountService {

	private final RiskAssessor riskAssessor;

	public DatabaseAccountService(RiskAssessor riskAssessor) {
		this.riskAssessor = riskAssessor;
	}

	// ...

}
```

| ![[小费]](https://docs.spring.io/spring-boot/docs/2.1.9.RELEASE/reference/html/images/tip.png) |
| ------------------------------------------------------------ |
| 请注意，使用构造函数注入如何将`riskAssessor`字段标记为`final`，指示该字段随后无法更改。 |