package top.kaoshanji.leaning.jdkx.io.net;

import java.net.InetAddress;
import java.util.concurrent.Callable;

/**
 * 解析一个日志文件项，查找一个地址，并把这个地址替换为相应的主机名
 * Weblog 线程版
 * @author kaoshanji
 * @time 2020/1/14 17:15
 */
public class LookupTask implements Callable<String> {

    private String line;

    public LookupTask(String line) {
        this.line = line;
    }

    @Override
    public String call() throws Exception {

        try {
            int index = line.indexOf(" ");
            String address = line.substring(0, index);
            String theRest = line.substring(index);
            String hostname = InetAddress.getByName(address).getHostName();
            return hostname + " " + theRest;
        } catch (Exception ex) {
            return line;
        }
    }
}
