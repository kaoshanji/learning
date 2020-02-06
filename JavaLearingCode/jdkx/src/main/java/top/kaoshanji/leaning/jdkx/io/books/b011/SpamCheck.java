package top.kaoshanji.leaning.jdkx.io.books.b011;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * 检查垃圾邮件发送者
 * @author kaoshanji
 * @time 2020/2/5 下午3:49
 */
public class SpamCheck {

    private final static Logger logger = LoggerFactory.getLogger(SpamCheck.class);

    public static final String BLACKHOLE = "sbl.spamhaus.org";

    public static void main(String[] args) throws UnknownHostException {
        for (String arg: args) {
            if (isSpammer(arg)) {
                logger.info(arg + " is a known spammer.");
            } else {
                logger.info(arg + " appears legitimate.");
            }
        }
    }

    /**
     * 出现异常说明不是垃圾邮件发送者
     * BLACKHOLE 服务器请求和响应可能有变化
     * @param arg
     * @return
     */
    private static boolean isSpammer(String arg) {
        try {
            InetAddress address = InetAddress.getByName(arg);
            byte[] quad = address.getAddress();
            String query = BLACKHOLE;
            for (byte octet : quad) {
                int unsignedByte = octet < 0 ? octet + 256 : octet;
                query = unsignedByte + "." + query;
            }
            InetAddress.getByName(query);
            return true;
        } catch (UnknownHostException e) {
            return false;
        }
    }


}
