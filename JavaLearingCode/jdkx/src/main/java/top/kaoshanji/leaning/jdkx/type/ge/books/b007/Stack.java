package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

/**
 * 栈-泛型
 * @author kaoshanji
 * @time 2020年3月18日 下午10:01:45
 */

public class Stack<T> {
	
	protected T[] stack = (T[]) new Object[100];
	int ptr = -1;
	
	void push(T data) {
		ptr++;
		stack[ptr] = data;
	}
	
	T pop() {
		return (T)stack[ptr--];
	}
	
}
