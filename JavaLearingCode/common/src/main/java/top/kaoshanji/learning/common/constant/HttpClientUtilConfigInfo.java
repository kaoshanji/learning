package top.kaoshanji.learning.common.constant;

import com.google.gson.Gson;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 常量参数
 *
 * @author xupeng
 * @time 2018年7月17日上午10:53:30
 */
public class HttpClientUtilConfigInfo {

    private static String EN_DEVELOP = "develop";	// 开发
    private static String EN_TEST = "test";			// 测试
    private static String EN_JOIN = "join"; 		// 联调
    private static String EN_SHOP = "shop"; 		// shop

    public static String ENVIRONMENT = EN_DEVELOP; 	// 对外环境

    public static String projectClassFile = System.getProperty("user.dir") + "f-ftargetf-fclassesf-ffilef-f".replace("f-f", File.separator);

    /**
     * app完整路径
     * ....
     * @param environment
     * @return
     */
    public static String getShopUrl(String environment) {
        return getRootUrl(environment) + getAppShopUrl(environment);
    }

    public static String getShopAdminUrl(String environment) {
        return getRootUrl(environment) + getAppShopAdminUrl(environment);
    }

    public static String getOperateUrl(String environment) {
        return getRootUrl(environment) + getAppOperateUrl(environment);
    }

    public static String getPointsUrl(String environment) {
        return getRootUrl(environment) + getAppPointsUrl(environment);
    }

    public static String getDistributeUrl(String environment) {
        return getRootUrl(environment) + getAppDistributeUrl(environment);
    }

    /**
     * 获得 Gson
     *
     * @return
     */
    public static Gson getGson() {
        return new Gson();
    }

    /**
     * 创建 Cookie 时数据
     * @param environment
     * @return
     */
    public static String getCookieDomain(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, "localhost");
        map.put(EN_TEST, "test.com");
        return map.get(environment);
    }

    ///////////////////////////私有///数据源///////////////////////////////////////

    /**
     * 环境 URL
     * @param environment 环境模式
     * @return
     */
    private static String getRootUrl(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, "http://localhost");
        map.put(EN_TEST, "");
        map.put(EN_JOIN, "http://192.168.1.116");
        map.put(EN_SHOP, "");
        return map.get(environment);
    }

    /**
     * xx项目 URL
     * @param environment 环境模式
     * @return
     */
    private static String getAppShopUrl(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, ":8200/passpay-shop");
        map.put(EN_TEST, "behindshop");
        map.put(EN_JOIN, ":9080/passpay-shop");
        map.put(EN_SHOP, "behindshop");
        return map.get(environment);
    }

    private static String getAppShopAdminUrl(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, ":8202/passpay-shop-admin");
        map.put(EN_TEST, "behindshopadmin");
        map.put(EN_JOIN, ":9380/passpay-shop-admin");
        map.put(EN_SHOP, "behindshopadmin");
        return map.get(environment);
    }

    private static String getAppOperateUrl(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, ":8302/passpay-operate");
        //	map.put(EN_TEST, "admin/");
        return map.get(environment);
    }

    private static String getAppPointsUrl(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, ":8402/passpay-points");
        map.put(EN_TEST, "behindpoints");
        map.put(EN_JOIN, ":9480/passpay-shop-admin");
        return map.get(environment);
    }

    private static String getAppDistributeUrl(String environment) {
        Map<String, String> map = new HashMap<>();
        map.put(EN_DEVELOP, ":8502/passpay-distribute");
        map.put(EN_TEST, "behinddistribute");
        map.put(EN_JOIN, ":9480/passpay-shop-admin");
        return map.get(environment);
    }

}
