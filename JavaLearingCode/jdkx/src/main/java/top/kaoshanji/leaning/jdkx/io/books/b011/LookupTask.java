package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * 解析一个日志文件项，查找一个地址
 * @author kaoshanji
 * @time 2020/2/5 下午4:15
 */
public class LookupTask implements Callable<String> {

    private String line;

    public LookupTask(String line) {
        this.line = line;
    }

    @Override
    public String call() throws Exception {
        try {
            // 分解 IP 地址
            int index = line.indexOf(' ');
            String address = line.substring(0, index);
            String theRest = line.substring(index);
            String hostname = InetAddress.getByName(address).getHostName();
            return hostname + " " + theRest;
        } catch (Exception ex) {
            return line;
        }
    }
}
