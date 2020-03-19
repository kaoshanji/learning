package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

/**
 * NumberStackDemoApp
 * @author kaoshanji
 * @time 2020年3月19日 下午4:14:23
 */
public class NumberStackDemoApp {
	
	// ? 通配符..表示任意继承自 Number 的子类都是符合要求
	static void dumpStack(NumberStack<?> stack) {
		for (Number n : stack.getStack()) {
			System.out.println(n);
		}
	}

	public static void main(String[] args) {

		// long 类型
		System.out.println("....添加long...类型");
		NumberStack<Long> longStack = new NumberStack<Long>();
		longStack.push(5L);
		longStack.push(15L);
		dumpStack(longStack);
		
		
		// Number 类型
		System.out.println("....添加Number...类型");
		NumberStack<Number> numberStack = new NumberStack<Number>();
		numberStack.push(22L);
		numberStack.push(25L);
		dumpStack(numberStack);
		
		// Integer 类型
		System.out.println("....添加Integer...类型");
		NumberStack<Integer> integerStack = new NumberStack<Integer>();
		integerStack.push(2);
		integerStack.push(5);
		dumpStack(integerStack);
		
		
	}

}
