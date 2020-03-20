package top.kaoshanji.leaning.jdkx.type.ge.books.b001;

/**
 * 定义泛型方法
 * 比较字符串
 * @author kaoshanji
 * @time 2020年3月20日 下午2:15:39
 */
public class ArrayAlg {
	
	public static Pair<String> minmax(String[] a) {
		if (a == null || a.length == 0) {
			return null;
		}
		String min = a[0];
		String max = a[0];
		
		for (int i = 1; i < a.length; i++) {
			if (min.compareTo(a[i]) > 0) {
				min = a[i];
			}
			
			if (max.compareTo(a[i]) < 0) {
				max = a[i];
			}
		}
		return new Pair<String>(min, max);
		
	}

}
