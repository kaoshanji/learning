# 来自命令行的I / O.

程序通常从命令行运行，并在命令行环境中与用户交互。Java平台以两种方式支持这种交互：通过标准流和通过控制台。

## 标准流

标准流是许多操作系统的一个特性。默认情况下，他们从键盘读取输入并将输出写入显示器。它们还支持文件和程序之间的I / O，但该功能由命令行解释器控制，而不是程序。

Java平台支持三种标准流：*标准输入*，通过访问`System.in`; *标准输出*，通过访问`System.out`; 和*标准错误*，通过访问`System.err`。这些对象是自动定义的，不需要打开。标准输出和标准误差均用于输出; 单独具有错误输出允许用户将常规输出转移到文件并仍然能够读取错误消息。有关更多信息，请参阅命令行解释程序的文档。

您可能希望标准流是字符流，但由于历史原因，它们是字节流。`System.out`并被`System.err`定义为 [`PrintStream`](https://docs.oracle.com/javase/8/docs/api/java/io/PrintStream.html)对象。虽然从技术上讲它是字节流，但`PrintStream`利用内部字符流对象来模拟字符流的许多功能。

相比之下，`System.in`是一个没有字符流功能的字节流。使用标准输入作为字符流，包裹`System.in`在`InputStreamReader`。

```java
InputStreamReader cin = new InputStreamReader(System.in);
```

## 控制台

控制台是标准流的更高级替代方案。这是一个预定义的单个对象， [`Console`](https://docs.oracle.com/javase/8/docs/api/java/io/Console.html)它具有标准流提供的大部分功能，以及其他功能。控制台对于安全密码输入特别有用。控制台对象还提供了为真字符流，通过其输入和输出流`reader`和`writer`方法。

在程序可以使用控制台之前，它必须尝试通过调用来检索Console对象`System.console()`。如果Console对象可用，则此方法返回该对象。如果`System.console`返回`NULL`，则不允许控制台操作，因为操作系统不支持它们，或者因为程序是在非交互式环境中启动的。

Console对象支持通过其`readPassword`方法输入安全密码。此方法有助于以两种方式保护密码输入。首先，它抑制回显，因此密码在用户屏幕上不可见。其次，`readPassword`返回一个字符数组，而不是a `String`，因此密码可以被覆盖，一旦不再需要就将其从内存中删除。

该 [`Password`](examples/Password.java)示例是用于更改用户密码的原型程序。它演示了几种`Console`方法。

```java
import java.io.Console;
import java.util.Arrays;
import java.io.IOException;

public class Password {
    
    public static void main (String args[]) throws IOException {

        Console c = System.console();
        if (c == null) {
            System.err.println("No console.");
            System.exit(1);
        }

        String login = c.readLine("Enter your login: ");
        char [] oldPassword = c.readPassword("Enter your old password: ");

        if (verify(login, oldPassword)) {
            boolean noMatch;
            do {
                char [] newPassword1 = c.readPassword("Enter your new password: ");
                char [] newPassword2 = c.readPassword("Enter new password again: ");
                noMatch = ! Arrays.equals(newPassword1, newPassword2);
                if (noMatch) {
                    c.format("Passwords don't match. Try again.%n");
                } else {
                    change(login, newPassword1);
                    c.format("Password for %s changed.%n", login);
                }
                Arrays.fill(newPassword1, ' ');
                Arrays.fill(newPassword2, ' ');
            } while (noMatch);
        }

        Arrays.fill(oldPassword, ' ');
    }
    
    // Dummy change method.
    static boolean verify(String login, char[] password) {
        // This method always returns
        // true in this example.
        // Modify this method to verify
        // password according to your rules.
        return true;
    }

    // Dummy change method.
    static void change(String login, char[] password) {
        // Modify this method to change
        // password according to your rules.
    }
}
```

该`Password`课程遵循以下步骤：

1. 尝试检索Console对象。如果对象不可用，则中止。
2. 调用`Console.readLine`以提示并读取用户的登录名。
3. 调用`Console.readPassword`以提示并读取用户的现有密码。
4. 调用`verify`以确认用户有权更改密码。（在此示例中，`verify`是一个始终返回的虚方法`true`。）
5. 重复以下步骤，直到用户输入两次相同的密码：
   1. 调用`Console.readPassword`两次以提示并读取新密码。
   2. 如果用户两次输入相同的密码，请调用`change`以进行更改。（再次，`change`是一种虚拟方法。）
   3. 用空格覆盖两个密码。
6. 用空格覆盖旧密码。