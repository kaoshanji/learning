package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

/**
 * 栈-泛型-限定类型
 * @author kaoshanji
 * @time 2020年3月19日 下午2:36:01
 */
public class NumberStack<T extends Number> {
	
	private Number stack [] = new Number[5];
	int ptr = -1;
	
	public Number[] getStack() {
		return stack;
	}
	
	void push(T data) {
		ptr++;
		stack[ptr] = data;
	}
	
	T pop() {
		return (T)stack[ptr--];
	}


}
