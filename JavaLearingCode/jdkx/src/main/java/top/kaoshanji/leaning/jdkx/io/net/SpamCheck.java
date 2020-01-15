package top.kaoshanji.leaning.jdkx.io.net;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 检测地址是否是一个已知的垃圾邮件发送者
 * @author kaoshanji
 * @time 2020/1/14 16:04
 */
public class SpamCheck {

    private final static Logger logger = LoggerFactory.getLogger(SpamCheck.class);

    // 黑洞服务器
    public static final String BLACKHOLE = "sbl.spamhaus.org";


    public static void main(String[] args) throws UnknownHostException {
        String ip = "207.34.56.23";
        if (isSpammer(ip)) {
            logger.info(ip + " is a known spammer.");
        } else {
            logger.info(ip + " appears legitimate.");
        }


    }

    /**
     * 检测ip是否是垃圾邮件发送者
     * @param ip
     * @return
     * @throws UnknownHostException
     */
    private static boolean isSpammer(String ip) throws UnknownHostException {

        InetAddress address = InetAddress.getByName(ip);
        byte [] quad = address.getAddress();
        String query = BLACKHOLE;
        for (byte octct : quad) {
            // IP 地址 从 byte --> int
            // byte < 128 时成为负数了..
            int unsignedByte = octct < 0 ? octct + 256 : octct;
            query = unsignedByte + "." + query;
        }
        logger.info("...query:"+query);
        InetAddress.getByName(query);
        return true;
    }


}
