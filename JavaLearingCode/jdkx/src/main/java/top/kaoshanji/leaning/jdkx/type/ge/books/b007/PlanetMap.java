package top.kaoshanji.leaning.jdkx.type.ge.books.b007;

import java.util.HashMap;
import java.util.Map;

/**
 * 太阳系中行星的哈希表
 * 使用两个类型参数的HashMap类的程序
 * @author kaoshanji
 * @time 2020年3月20日 上午9:41:18
 */
public class PlanetMap {

	public static void main(String[] args) {
		
		Map<Integer, String> mapOFPlanets = new HashMap<>();
		
		mapOFPlanets.put(1, "Me");
		mapOFPlanets.put(2, "Ve");
		mapOFPlanets.put(3, "Ea");
		mapOFPlanets.put(4, "Ma");
		mapOFPlanets.put(5, "Ju");
		mapOFPlanets.put(6, "Sa");
		mapOFPlanets.put(7, "Ur");
		mapOFPlanets.put(8, "Ne");
		
		for (Map.Entry<Integer, String> entry : mapOFPlanets.entrySet()) {
			System.out.println("..mapOFPlanets.K:"+entry.getKey() +"...V:"+entry.getValue());
		}
	}

}
