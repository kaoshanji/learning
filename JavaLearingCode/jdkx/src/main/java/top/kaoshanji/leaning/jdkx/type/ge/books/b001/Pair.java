package top.kaoshanji.leaning.jdkx.type.ge.books.b001;

/**
 * 定义一个泛型类
 * @author kaoshanji
 * @time 2020年3月20日 下午1:45:24
 */
public class Pair<T> {
	
	private T first;
	private T second;
	
	public Pair() {
		first = null;
		second = null;
	}

	public Pair(T first, T second) {
		this.first = first;
		this.second = second;
	}

	public T getFirst() {
		return first;
	}

	public void setFirst(T first) {
		this.first = first;
	}

	public T getSecond() {
		return second;
	}

	public void setSecond(T second) {
		this.second = second;
	}
	
}
