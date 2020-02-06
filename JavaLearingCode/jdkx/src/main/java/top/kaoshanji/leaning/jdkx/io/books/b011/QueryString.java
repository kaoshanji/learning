package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

/**
 * 拼接字符串
 * POST 提交
 * @author kaoshanji
 * @time 2020/2/5 下午10:42
 */
public class QueryString {

    private StringBuilder query = new StringBuilder();

    public QueryString() {
    }

    public synchronized void add(String name, String value) {
        query.append('&');
        encode(name, value);
    }

    private synchronized void encode(String name, String value) {
        try {
            query.append(URLEncoder.encode(name, "UTF-8"));
            query.append('=');
            query.append(URLEncoder.encode(value, "UTF-8"));
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException("Broken VM does not support UTF-8");
        }
    }

    public synchronized String getQuery() {
        return query.toString();
    }

    @Override
    public String toString() {
        return getQuery();
    }

}
