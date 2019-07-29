package top.kaoshanji.learning.common.util;

import org.junit.Before;
import org.junit.Test;
import top.kaoshanji.learning.common.constant.HttpClientUtilConfigInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * HttpClientUtil 使用示例
 * @author kaoshanji
 * @time 2019/7/29 15:00
 */
public class HttpClientUtilTest {


    // api 路径
    String appUri = "";

    @Before
    public void before() {
        appUri = HttpClientUtilConfigInfo.getShopAdminUrl(HttpClientUtilConfigInfo.ENVIRONMENT)+"/shopAdmin/tool/";
    }

    // GET 请求
    @Test
    public void getToolInfoList() {

        String url = appUri + "getToolInfoList";

        HttpClientUtil.executeGet(url);
    }

    // POST 请求
    @Test
    public void updateToolInfoShopWarrant() {

        String url = appUri + "updateToolInfoShopWarrant";

        Map<String,Object> map = new HashMap();
        map.put("id", 1);
        map.put("shopId", 7);
        map.put("whetherLimited", 2);

        HttpClientUtil.executePost(url, HttpClientUtilConfigInfo.getGson().toJson(map));
    }

}
