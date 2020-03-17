package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * 使用内置的泛型类
 * @author kaoshanji
 * @time 2020年3月17日 下午8:55:12
 */
public class FamousQuotes {
	
	// 不使用泛型的集合
	private static ArrayList listOfFamousQuotes;
	
	// 使用泛型的集合，类型是String，进行了类型检查
	private static ArrayList<String> listOfFamousQuotesTypechecked;

	void buildList() {
		listOfFamousQuotes = new ArrayList<>();
		listOfFamousQuotes.add("xxx1"); // 类型参数是 Object
		listOfFamousQuotes.add("xxx2");
		//listOfFamousQuotes.add(100); // 字符串和数字都能添加进行，然后遍历就异常了
	}
	
	void printList() {
		buildList();
		Iterator it = listOfFamousQuotes.iterator();
		while(it.hasNext()) {
			String quote = (String)it.next(); // 需要强制转换
			System.out.println(quote);
		}
		
	}
	
	void buildCheckedList() {
		listOfFamousQuotesTypechecked = new ArrayList<>();
		listOfFamousQuotesTypechecked.add("xxp1");
		listOfFamousQuotesTypechecked.add("xxp2");
	//	listOfFamousQuotesTypechecked.add(1000); // 编译器报错
		listOfFamousQuotesTypechecked.add("xxp3");
	}
	
	void printCheckedList() {
		buildCheckedList();
		Iterator<String> it = listOfFamousQuotesTypechecked.iterator();
		while(it.hasNext()) {
			String quote = it.next(); // 不需要强制转换
			System.out.println(quote);
		}
	}
	
	public static void main(String[] args) {

		FamousQuotes app = new FamousQuotes();
		
		System.out.println(".....printList......");
		app.printList();
		
		System.out.println(".....printCheckedList......");
		app.printCheckedList();
		
	}

}
