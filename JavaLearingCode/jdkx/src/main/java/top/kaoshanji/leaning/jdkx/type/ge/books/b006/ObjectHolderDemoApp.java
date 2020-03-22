package top.kaoshanji.leaning.jdkx.type.ge.books.b006;

/**
 * ObjectHolderDemoApp
 * @author kaoshanji
 * @time 2020年3月22日 下午12:10:30
 */

public class ObjectHolderDemoApp {

	public static void main(String[] args) {

		ObjectHolder<String> holder = new ObjectHolder<String>();
		holder.setObj("Hello");
		String str = holder.getObj();
		
		System.out.println("holder:"+str);
	}

}
