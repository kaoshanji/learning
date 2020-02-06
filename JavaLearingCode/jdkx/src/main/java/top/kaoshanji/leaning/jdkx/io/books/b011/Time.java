package top.kaoshanji.leaning.jdkx.io.books.b011;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Date;

/**
 * 时间协议客户端
 * 网络代码比较相识，但是解析数据是不同的
 * 服务器响应的数据是 32位无符号大端二进制树发送
 * @author kaoshanji
 * @time 2020/2/6 下午1:14
 */
public class Time {

    private static final String HOSTNAME = "time.nist.gov";

    public static void main(String[] args) throws IOException, ParseException {
        Date d = Time.getDateFromNetwork();
        System.out.println("It is " + d);
    }

    public static Date getDateFromNetwork() throws IOException, ParseException {
        // 时间协议设置时间起点为 1900年
        // Java Date 类起始于 1970年，
        // 使用下面的数字进行转换

        long differenceBetweenEpochs = 2208988800L;

        // 不愿意使用魔法数计算就注释掉
        // 以下代码的注释，这段代码会直接进行计算
    /*
    TimeZone gmt = TimeZone.getTimeZone("GMT");
    Calendar epoch1900 = Calendar.getInstance(gmt);
    epoch1900.set(1900, 01, 01, 00, 00, 00);
    long epoch1900ms = epoch1900.getTime().getTime();
    Calendar epoch1970 = Calendar.getInstance(gmt);
    epoch1970.set(1970, 01, 01, 00, 00, 00);
    long epoch1970ms = epoch1970.getTime().getTime();

    long differenceInMS = epoch1970ms - epoch1900ms;
    long differenceBetweenEpochs = differenceInMS/1000;
    */

        Socket socket = null;
        try {
            socket = new Socket(HOSTNAME, 37);
            socket.setSoTimeout(15000);

            InputStream raw = socket.getInputStream();

            long secondsSince1900 = 0;
            for (int i = 0; i < 4; i++) {
                secondsSince1900 = (secondsSince1900 << 8) | raw.read();
            }

            long secondsSince1970 = secondsSince1900 - differenceBetweenEpochs;
            long msSince1970 = secondsSince1970 * 1000;
            Date time = new Date(msSince1970);

            return time;
        } finally {
            try {
                if (socket != null) socket.close();
            }
            catch (IOException ex) {}
        }
    }

}
