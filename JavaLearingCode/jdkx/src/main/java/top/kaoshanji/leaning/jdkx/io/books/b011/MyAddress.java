package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 查找本机机器的地址
 * @author kaoshanji
 * @time 2020/2/5 下午3:00
 */
public class MyAddress {

    private final static Logger logger = LoggerFactory.getLogger(MyAddress.class);

    public static void main(String[] args) throws UnknownHostException {

        InetAddress address = InetAddress.getLocalHost();
        logger.info("本机机器的地址："+address);

    }

}
