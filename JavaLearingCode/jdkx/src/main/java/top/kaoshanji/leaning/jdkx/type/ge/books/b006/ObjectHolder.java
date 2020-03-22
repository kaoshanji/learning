package top.kaoshanji.leaning.jdkx.type.ge.books.b006;

/**
 * 声明泛型类型的示例
 * @author kaoshanji
 * @time 2020年3月22日 下午12:07:34
 */

public class ObjectHolder<T> {
	
	private T obj;

	public T getObj() {
		return obj;
	}

	public void setObj(T obj) {
		this.obj = obj;
	}
	
}
