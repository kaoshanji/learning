package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 创建 InetAddress 对象
 * 获取 主机名(www.oreilly.com) 地址
 * @author kaoshanji
 * @time 2020/2/5 下午2:51
 */
public class OReillyByName {

    private final static Logger logger = LoggerFactory.getLogger(OReillyByName.class);

    public static void main(String[] args) throws UnknownHostException {

        // 也可能一个主机名对应多个地址
        InetAddress address = InetAddress.getByName("www.oreilly.com");
        logger.info("www.oreilly.com地址："+address);

    }
}
