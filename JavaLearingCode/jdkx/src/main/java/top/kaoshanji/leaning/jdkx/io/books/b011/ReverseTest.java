package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 给定地址找出主机名
 * @author kaoshanji
 * @time 2020/2/5 下午3:15
 */
public class ReverseTest {

    private final static Logger logger = LoggerFactory.getLogger(MyAddress.class);

    public static void main(String[] args) throws UnknownHostException {

        InetAddress ia = InetAddress.getByName("23.198.82.12");

        logger.info("给定地址找出主机名："+ia.getCanonicalHostName());

    }

}
