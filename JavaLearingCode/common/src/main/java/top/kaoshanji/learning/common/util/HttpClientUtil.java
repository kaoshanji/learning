package top.kaoshanji.learning.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.CookieStore;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.AuthSchemes;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.cookie.BasicClientCookie;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import top.kaoshanji.learning.common.constant.HttpClientUtilConfigInfo;

/**
 * HttpClient对象
 * v-1.3.4
 * HTTP 客户端请求，可以用作API接口测试
 * 请求与响应都是JSON格式字符串
 * ps：token 需要手动获取并调用 writerToken(token)方法，属性默认 token
 * ps：session 需要在第一次登陆获取会话时传递 true，这样会保持会话标识在本地，后续 传递 false
 * @author xupeng
 * @time 2018年7月17日上午10:51:57
 */
public class HttpClientUtil {
	
	private final static Logger logger = LoggerFactory.getLogger(HttpClientUtil.class);
	
	/**
     * 根目录
     */
    public static String HTTP_CLIENT_LOCAL_ROOT = "F:\\";//System.getProperty("user.dir")+File.separator;

    /**
     * 会话文件
     */
    private static String  HTTPCLIENTUTIL_COOKIE = HTTP_CLIENT_LOCAL_ROOT+"httpClientUtil-cookie.properties";

    /**
     * Java Web会话标识
     */
    private static String   COOKIE_JSESSIONID = "JSESSIONID";

    /**
     * token 文件
     */
    private static String  HTTPCLIENTUTIL_TOKEN = HTTP_CLIENT_LOCAL_ROOT+"httpClientUtil-token.properties";

    /**
     * token属性
     */
    private static String   COOKIE_TOKEN    =   "TOKEN";

    /**
     * 地址栏或请求头 token属性
     */
    private static String   URL_TOKEN = "token";


    /**
     * 执行GET请求
     * 打印结果到日志..
     * @param url 请求地址
     */
    public static void  executeGet(String url) {
        HttpGet httpGet = new HttpGet(url);
        execute(httpGet);
    }

    /**
     * 执行POST请求
     * 打印结果到日志..
     * @param url 请求地址
     * @param parmJson JSON格式字符串
     */
    public static void executePost(String url, String parmJson) {

        HttpPost httpPost = getHttpPost(url, parmJson);

        logger.info(".............executing executePost parmJson: " + parmJson);

        execute(httpPost);
    }


    /**
     * 执行上传文件
     * 打印结果到日志..
     * @param url 请求地址
     * @param file File对象
     * @param fileName 请求文件属性名称
     */
    public static void executeFile(String url, File file , String fileName) {

        HttpPost httpPost = getHttpPostFile(url, file, fileName);

        execute(httpPost);
    }


    /**
     * 执行GET请求 会话
     * 打印结果到日志..
     * @param url 请求地址
     * @param isCookie true 还没cookie，false 已有cookie
     */
    public static void  executeGetSession(String url, Boolean isCookie) {
        HttpGet httpGet = new HttpGet(url);
        executeCookie(httpGet, isCookie);
    }

    /**
     * 执行POST请求 会话
     * 打印结果到日志..
     * @param url 请求地址
     * @param parmJson JSON格式字符串
     * @param isCookie true 还没cookie，false 已有cookie
     */
    public static void executePostSession(String url, String parmJson, Boolean isCookie) {
        HttpPost httpPost = getHttpPost(url, parmJson);
        logger.info(".............executing executePostSession parmJson: " + parmJson);
        executeCookie(httpPost, isCookie);
    }


    /**
     * 执行上传文件 会话
     * 打印结果到日志..
     * @param url 请求地址
     * @param file File对象
     * @param fileName 请求文件属性名称
     *  @param isCookie true 还没 cookie，false 已有 cookie
     */
    public static void executeFileSession(String url, File file , String fileName, Boolean isCookie) {
        HttpPost httpPost = getHttpPostFile(url, file, fileName);
        executeCookie(httpPost, isCookie);
    }

    /**
     * 执行GET请求 地址栏附带 token
     * 打印结果到日志..
     * @param url 请求地址
     */
    public static void  executeGetUrlToken(String url) {
        String urlToken = getUrlToken(url);

        HttpGet httpGet = new HttpGet(urlToken);
        execute(httpGet);
    }

    /**
     * 执行POST请求 地址栏附带 token
     * 打印结果到日志..
     * @param url 请求地址
     * @param parmJson JSON格式字符串
     */
    public static void executePostUrlToken(String url, String parmJson) {

        String urlToken = getUrlToken(url);

        HttpPost httpPost = getHttpPost(urlToken, parmJson);

        logger.info(".............executing executePost parmJson: " + parmJson);

        execute(httpPost);
    }


    /**
     * 执行上传文件 地址栏附带 token
     * 打印结果到日志..
     * @param url 请求地址
     * @param file File对象
     * @param fileName 请求文件属性名称
     */
    public static void executeFileUrlToken(String url, File file , String fileName) {

        String urlToken = getUrlToken(url);

        HttpPost httpPost = getHttpPostFile(urlToken, file, fileName);

        execute(httpPost);
    }

    /**
     * 执行GET请求 地址栏附带 token
     * 打印结果到日志..
     * @param url 请求地址
     */
    public static void  executeGetHeaderToken(String url) {
        String token = readToken();

        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader(URL_TOKEN, token);

        execute(httpGet);
    }

    /**
     * 执行POST请求 地址栏附带 token
     * 打印结果到日志..
     * @param url 请求地址
     * @param parmJson JSON格式字符串
     */
    public static void executePostHeaderToken(String url, String parmJson) {

        String token = readToken();

        HttpPost httpPost = getHttpPost(url, parmJson);
        httpPost.addHeader(URL_TOKEN, token);
        logger.info(".............executing executePost parmJson: " + parmJson);

        execute(httpPost);
    }

    /**
     * 执行上传文件 地址栏附带 token
     * 打印结果到日志..
     * @param url 请求地址
     * @param file File对象
     * @param fileName 请求文件属性名称
     */
    public static void executeFileHeaderToken(String url, File file , String fileName) {

        String token = readToken();

        HttpPost httpPost = getHttpPostFile(url, file, fileName);
        httpPost.addHeader(URL_TOKEN, token);

        execute(httpPost);
    }

    /**
     * 保存 token
     * @param token
     */
    public static void writerToken(String token) {

        try {
            if (StringUtils.isNotBlank(token)) {
                Files.deleteIfExists(Paths.get(HTTPCLIENTUTIL_TOKEN));
                Properties prop = new Properties();
                prop.setProperty(COOKIE_TOKEN,token);
                prop.store(new FileOutputStream(HTTPCLIENTUTIL_TOKEN), "save token");
            }

        } catch (IOException e) {
            logger.error(".........保存token信息异常..........."+e.getMessage());
            e.printStackTrace();
        }
    }
    
    ////////////////////////////底层方法///////////////////////////////////////////////////

    private static HttpPost getHttpPost(String url, String parmJson) {
        HttpPost httpPost = new HttpPost(url);

        httpPost.addHeader("Content-type","application/json; charset=utf-8");
        httpPost.setHeader("Accept", "application/json");
        httpPost.setEntity(new StringEntity(parmJson, Charset.forName("UTF-8")));

        return httpPost;
    }

    private static HttpPost getHttpPostFile(String url, File file , String fileName) {

        HttpPost httpPost = new HttpPost(url);

        FileBody bin = new FileBody(file);
        HttpEntity reqEntity = MultipartEntityBuilder.create().addPart(fileName, bin).build();
        httpPost.setEntity(reqEntity);

        return httpPost;
    }


    /**
     * 普通执行请求
     * @param requestBase
     * @return
     */
    private static CloseableHttpResponse execute(HttpRequestBase requestBase) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        logger.info(".............executing Host: " + requestBase.getURI().getHost());
        logger.info(".............executing Path: " + requestBase.getURI().getPath());

        try {

            httpclient = HttpClients.createDefault();
            response = httpclient.execute(requestBase);

            // 打印
            responseBody(response);

        } catch ( IOException e) {
            logger.error("............."+e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }


    /**
     * 具体干活
     * 执行请求
     * @param requestBase 请求对象
     * @param isCookie 会话标识
     * @return
     */
    @SuppressWarnings("resource")
	private static CloseableHttpResponse executeCookie(HttpRequestBase requestBase, Boolean isCookie) {
        CloseableHttpResponse response = null;
        CloseableHttpClient httpclient = null;

        logger.info(".............executing Host: " + requestBase.getURI().getHost());
        logger.info(".............executing Path: " + requestBase.getURI().getPath());

        try {

            if (null!=isCookie) {

                // 没有会话
                // 可用在登录接口
                // 取得会话信息后保存到本地文件
                if (isCookie) {
                    httpclient = HttpClients.createDefault();
                    response = httpclient.execute(requestBase);

                    String cookie = getCookieByResponse(response);

                    writerCookie(cookie);
                }

                // 本地已经保有会话
                // 获取会话信息
                if (!isCookie) {

                    String cookie = readCookie();
                    CookieStore cookieStore = getCookieStore(cookie);
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();

                    RequestConfig defaultRequestConfig = RequestConfig.custom()
                            .setCookieSpec(CookieSpecs.DEFAULT)
                            .setExpectContinueEnabled(true)
                            .setTargetPreferredAuthSchemes(Arrays.asList(AuthSchemes.NTLM, AuthSchemes.DIGEST))
                            .setProxyPreferredAuthSchemes(Arrays.asList(AuthSchemes.BASIC))
                            .build();

                    httpclient = HttpClients.custom()
                            .setDefaultCookieStore(cookieStore)
                            .setDefaultCredentialsProvider(credentialsProvider)
                            .setDefaultRequestConfig(defaultRequestConfig)
                            .build();

                    HttpClientContext context = HttpClientContext.create();
                    context.setCookieStore(cookieStore);
                    context.setCredentialsProvider(credentialsProvider);

                    response = httpclient.execute(requestBase, context);

                }

            }


        } catch ( IOException e) {
            logger.error("............."+e.getMessage());
        } finally {
            try {
                httpclient.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return response;
    }


    /**
     * 打印输出...
     * @param response
     */
    private static void responseBody(CloseableHttpResponse response) {

        logger.info("---------0.---------result----------start---------------------");
        logger.info("---------1.响应状态:"+response.getStatusLine());
        HttpEntity resEntity = response.getEntity();

        try {

            String responseBody = EntityUtils.toString(resEntity, "utf-8");

            logger.info("---------2.响应内容:"+responseBody);

            if (resEntity != null) {
                logger.info("---------3.响应长度: " + resEntity.getContentLength());
            }
            EntityUtils.consume(resEntity);

        } catch (IOException e) {
            e.printStackTrace();
        }

        logger.info("---------4.---------result----------end---------------------");

    }
    
    /**
     * 获取cookie
     * @param httpResponse
     * @return
     */
    private static String getCookieByResponse(HttpResponse httpResponse) {
        String result = "";

        if (null!=httpResponse) {
            // JSESSIONID
            String setCookie = httpResponse.getFirstHeader("Set-Cookie").getValue();
            String JSESSIONID = setCookie.substring("JSESSIONID=".length(), setCookie.indexOf(";"));

            result = JSESSIONID;
        }

        return result;
    }

    /**
     * 获取 CookieStore
     * @param cookie
     * @return
     */
    private static CookieStore getCookieStore(String cookie) {

        CookieStore cookieStore = null;

        if (StringUtils.isNotBlank(cookie)) {
            cookieStore = new BasicCookieStore();

            // 新建一个Cookie
            BasicClientCookie clientCookie = new BasicClientCookie("JSESSIONID", cookie);
            clientCookie.setVersion(0);
            clientCookie.setDomain(HttpClientUtilConfigInfo.getCookieDomain(HttpClientUtilConfigInfo.ENVIRONMENT));
            clientCookie.setPath("/");

            cookieStore.addCookie(clientCookie);
        }

        return cookieStore;

    }

    /**
     * 保存cookie到本地
     * @param cookie
     */
    private static void writerCookie(String cookie) {

        try {
            if (StringUtils.isNotBlank(cookie)) {
                Files.deleteIfExists(Paths.get(HTTPCLIENTUTIL_COOKIE));
                Properties prop = new Properties();
                prop.setProperty(COOKIE_JSESSIONID,cookie);
                prop.store(new FileOutputStream(HTTPCLIENTUTIL_COOKIE), "save cookie");
            }

        } catch (IOException e) {
            logger.error(".........获取会话信息异常..........."+e.getMessage());
            e.printStackTrace();
        }
    }


    /**
     * 读取cookie
     * @return String cookie值
     */
    private static String readCookie() {

        String result = "";
        Path path = Paths.get(HTTPCLIENTUTIL_COOKIE);
        if (null!=path&&Files.exists(path)) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(HTTPCLIENTUTIL_COOKIE));
                String  JSESSIONID = prop.getProperty(COOKIE_JSESSIONID);
                result = JSESSIONID;
            } catch (IOException e) {
                logger.error(".........读取会话文件异常..........."+e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * 设置 token
     * @param url
     * @return
     */
    private static String getUrlToken(String url) {
        String result = "";
        StringBuilder stringBuilder = new StringBuilder();
        String token = readToken();
        if (url.contains("&")) {
            stringBuilder.append("&").append(URL_TOKEN).append("=").append(token);
        }

        if (!url.contains("&")) {
            stringBuilder.append("?").append(URL_TOKEN).append("=").append(token);
        }
        result = stringBuilder.toString();
        return result;
    }


    /**
     * 读取 token
     * @return String token 值
     */
    private static String readToken() {

        String result = "";
        Path path = Paths.get(HTTPCLIENTUTIL_TOKEN);
        if (null!=path&&Files.exists(path)) {
            try {
                Properties prop = new Properties();
                prop.load(new FileInputStream(HTTPCLIENTUTIL_TOKEN));

                String  token = prop.getProperty(COOKIE_TOKEN);

                result = token;

            } catch (IOException e) {
                logger.error(".........读取token文件异常..........."+e.getMessage());
                e.printStackTrace();
            }
        }

        return result;
    }

    public static void main(String [] args)  {

        //  Path path = Paths.get(System.getProperty("user.dir")+File.separator+"HttpClientUtil.properties");

        //   ConfigInfo.getGson().newJsonWriter(Files.newBufferedWriter(path,Charset.forName("UTF-8")));


        //   Map<String,String> map = new HashMap<>(3);

        //   Files.write(path, Arrays.asList("123"),Charset.forName("UTF-8"));


       /* Properties prop = new Properties();
        prop.setProperty("k1","11");
        prop.setProperty("k2","21");
        prop.setProperty("k3","31");

        prop.store(new FileOutputStream(System.getProperty("user.dir")+File.separator+"HttpClientUtil.properties"), "save cookie");
*/
        /*Configurations configs = new Configurations();

        Files.deleteIfExists(Paths.get(HTTPCLIENTUTIL_COOKIE));
        Files.createFile(Paths.get(HTTPCLIENTUTIL_COOKIE));

        FileBasedConfigurationBuilder<PropertiesConfiguration> builder = configs.propertiesBuilder(HTTPCLIENTUTIL_COOKIE);
        PropertiesConfiguration config = builder.getConfiguration();

        config.setProperty("k1","1111");
        builder.save();*/


       /* Configuration config = configs.properties(HTTPCLIENTUTIL_COOKIE);

        config.setProperty("k1","11");*/



    }

}
