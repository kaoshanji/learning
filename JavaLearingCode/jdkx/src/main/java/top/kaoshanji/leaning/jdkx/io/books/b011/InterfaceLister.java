package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 列出本机所有网络接口
 * @author kaoshanji
 * @time 2020/2/5 下午3:36
 */
public class InterfaceLister {

    private final static Logger logger = LoggerFactory.getLogger(InterfaceLister.class);

    public static void main(String[] args) throws SocketException {

        Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
        while (interfaces.hasMoreElements()) {
            NetworkInterface ni = interfaces.nextElement();
            logger.info("本地网络接口："+ni);
        }


    }



}
