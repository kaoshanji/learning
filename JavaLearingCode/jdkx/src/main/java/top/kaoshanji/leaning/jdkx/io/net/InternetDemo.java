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
     * 查找本地机器的IP地址
     * 一个真正的主机名和IP地址
     * @throws UnknownHostException
     */
    public void getHostAddress() throws UnknownHostException {
        InetAddress address = InetAddress.getLocalHost();
        logger.info("address:"+address.getHostAddress());
    }


    /**
     * 确定IP地址是 IPv4还是IPv6
     * @param inetAddress
     * @return
     */
    public int getVersion(InetAddress inetAddress) {
        int length = -1;
        byte [] address = inetAddress.getAddress();
        if (address.length == 4) {
            return 4;
        }

        if (address.length == 6) {
            return 6;
        }
        return length;
    }

    /**
     * 地址类型
     * @throws UnknownHostException
     */
    public void ipCharacteristics() throws UnknownHostException {
        InetAddress address = InetAddress.getByName("");

        logger.info("是否是通配地址，可以匹配本地系统中的任何地址:"+address.isAnyLocalAddress());
        logger.info("是否是回送地址，直接在IP层连接同一台计算机:"+address.isLoopbackAddress());
        logger.info("是否是IPv6本地连接地址:"+address.isLinkLocalAddress());
        logger.info("是否是IPv6本地网络地址:"+address.isSiteLocalAddress());

        logger.info("是否是一个组播地址:"+address.isMulticastAddress());
        logger.info("是否是一个组织范围组播地址:"+address.isMCOrgLocal());
        logger.info("是否是一个网站范围组播地址:"+address.isMCSiteLocal());
        logger.info("是否是一个子网范围组播地址:"+address.isMCLinkLocal());
        logger.info("是否是一个本地接口组播地址:"+address.isMCNodeLocal());

    }




}
