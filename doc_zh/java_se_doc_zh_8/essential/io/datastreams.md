# 数据流

数据流支持基本数据类型值的二进制I / O（ ，`boolean`，`char`，`byte`，`short`，`int`，`long`，`float`和`double`），以及字符串值。所有数据流都实现 [`DataInput`](https://docs.oracle.com/javase/8/docs/api/java/io/DataInput.html)接口或 [`DataOutput`](https://docs.oracle.com/javase/8/docs/api/java/io/DataOutput.html)接口。本节重点介绍这些接口最广泛使用的实现， [`DataInputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/DataInputStream.html)以及 [`DataOutputStream`](https://docs.oracle.com/javase/8/docs/api/java/io/DataOutputStream.html)。

该 [`DataStreams`](examples/DataStreams.java)示例通过写出一组数据记录然后再次读取它们来演示数据流。每条记录包含三个与发票上的项目相关的值，如下表所示：

| Order in record | Data type | Data description | Output Method                  | Input Method                 | Sample Value     |
| --------------- | --------- | ---------------- | ------------------------------ | ---------------------------- | ---------------- |
| 1               | `double`  | Item price       | `DataOutputStream.writeDouble` | `DataInputStream.readDouble` | `19.99`          |
| 2               | `int`     | Unit count       | `DataOutputStream.writeInt`    | `DataInputStream.readInt`    | `12`             |
| 3               | `String`  | Item description | `DataOutputStream.writeUTF`    | `DataInputStream.readUTF`    | `"Java T-Shirt"` |

让我们来看看关键代码吧`DataStreams`。首先，程序定义了一些常量，包含数据文件的名称和将写入的数据：

```java
static final String dataFile = "invoicedata";

static final double[] prices = { 19.99, 9.99, 15.99, 3.99, 4.99 };
static final int[] units = { 12, 8, 13, 29, 50 };
static final String[] descs = {
    "Java T-shirt",
    "Java Mug",
    "Duke Juggling Dolls",
    "Java Pin",
    "Java Key Chain"
};
```

然后`DataStreams`打开输出流。由于`DataOutputStream`只能将a创建为现有字节流对象的包装器，因此`DataStreams`提供缓冲文件输出字节流。

```java
out = new DataOutputStream(new BufferedOutputStream(
              new FileOutputStream(dataFile)));
```

`DataStreams` 写出记录并关闭输出流。

```java
for (int i = 0; i < prices.length; i ++) {
    out.writeDouble(prices[i]);
    out.writeInt(units[i]);
    out.writeUTF(descs[i]);
}
```

该`writeUTF`方法`String`以UTF-8的修改形式写出值。这是一个可变宽度的字符编码，只需要一个字节用于常见的西方字符。

现在`DataStreams`再次读回数据。首先，它必须提供输入流和变量来保存输入数据。就像`DataOutputStream`，`DataInputStream`必须构造为字节流的包装器。

```java
in = new DataInputStream(new
            BufferedInputStream(new FileInputStream(dataFile)));

double price;
int unit;
String desc;
double total = 0.0;
```

现在`DataStreams`可以读取流中的每条记录，报告它遇到的数据。

```java
try {
    while (true) {
        price = in.readDouble();
        unit = in.readInt();
        desc = in.readUTF();
        System.out.format("You ordered %d" + " units of %s at $%.2f%n",
            unit, desc, price);
        total += unit * price;
    }
} catch (EOFException e) {
}
```

请注意，`DataStreams`通过捕获[`EOFException`](https://docs.oracle.com/javase/8/docs/api/java/io/EOFException.html)而不是测试无效的返回值来检测文件结束条件 。`DataInput`方法的所有实现都使用`EOFException`而不是返回值。

另请注意，每个专门`write`的`DataStreams`都与相应的专业匹配`read`。程序员需要确保输出类型和输入类型以这种方式匹配：输入流由简单的二进制数据组成，没有任何内容可以指示单个值的类型，也不是它们在流中开始的位置。

`DataStreams`使用一种非常糟糕的编程技术：它使用浮点数来表示货币值。通常，浮点对于精确值是不利的。对于小数部分尤其如此，因为常见值（例如`0.1`）没有二进制表示。

用于货币值的正确类型是 [`java.math.BigDecimal`](https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html)。不幸的是，它`BigDecimal`是一种对象类型，因此它不适用于数据流。但是，`BigDecimal` *将*使用对象流，这将在下一节中介绍。