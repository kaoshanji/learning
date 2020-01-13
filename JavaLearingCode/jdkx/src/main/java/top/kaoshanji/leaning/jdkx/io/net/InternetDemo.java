package top.kaoshanji.leaning.jdkx.io.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Internet 示例
 * @author kaoshanji
 * @time 2020/1/13 17:26
 */
public class InternetDemo {

    private final static Logger logger = LoggerFactory.getLogger(InternetDemo.class);

    /**
     * 创建 InetAddress
     * 根据域名返回IP地址
     * @param wwwName
     * @throws UnknownHostException
     */
    public void getAddressByName(String wwwName) throws UnknownHostException {
        InetAddress address = InetAddress.getByName(wwwName);
        logger.info("address:"+address);
    }

    /**
     * 查询一个主机的所有IP地址
     * @param wwwName
     * @throws UnknownHostException
     */
    public void getAllAddressByName(String wwwName) throws UnknownHostException {
        InetAddress [] addresses = InetAddress.getAllByName(wwwName);
        for (InetAddress address : addresses) {
            logger.info("address:"+address);
        }
    }

    /**
     * 查找本地机器的地址
     * 一个真正的主机名和IP地址
     * @throws UnknownHostException
     */
    public void getLocalHost() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        logger.info("address:"+address);
    }


}
