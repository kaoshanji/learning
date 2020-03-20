package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

/**
 * 比较和赋值
 * @author kaoshanji
 * @time 2020年3月20日 上午9:58:53
 */
public class UniverseTypeDemoApp {

	public static void main(String[] args) {
		
		UniverseType<Float> ff = new UniverseType<Float>(5f);
		UniverseType<Double> dd = new UniverseType<Double>(5.0);
		
		System.out.println(ff.equals(dd));
		
		

	}

}
