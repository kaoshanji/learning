package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

import java.math.BigDecimal;

/**
 * StackDemoApp
 * @author kaoshanji
 * @time 2020年3月19日 上午11:03:31
 */
public class StackDemoApp {

	public static void main(String[] args) {
		
		// long 类型
		System.out.println("....添加long...类型");
		Stack<Long> longStack = new Stack<Long>();
		longStack.push(5l);
		longStack.push(10l);
		longStack.push(12l);
		System.out.println("....打印...longStack");
		System.out.println(longStack.pop());
		System.out.println(longStack.pop());
		System.out.println(longStack.pop());
		System.out.println("------------------");
		
		// BigDecimal 类型
		System.out.println("....添加bigDecimalStack...类型");
		Stack<BigDecimal> bigDecimalStack = new Stack<BigDecimal>();
		bigDecimalStack.push(new BigDecimal(12.1));
		bigDecimalStack.push(new BigDecimal(13.1));
		bigDecimalStack.push(new BigDecimal(14.1));
		System.out.println("....打印...bigDecimalStack..转成double类型");
		System.out.println(bigDecimalStack.pop().doubleValue());
		System.out.println(bigDecimalStack.pop().doubleValue());
		System.out.println(bigDecimalStack.pop().doubleValue());
		System.out.println("------------------");
		
	}

}
