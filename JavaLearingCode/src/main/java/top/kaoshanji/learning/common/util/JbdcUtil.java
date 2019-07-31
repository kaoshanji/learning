package top.kaoshanji.learning.common.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

/**
 * JDBC ..
 * @author kaoshanji
 * @time 2019/7/29 15:15
 */
public class JbdcUtil {

    /**
     * 获得 JDBC 连接
     * @return Connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection() throws ClassNotFoundException, SQLException {
        Connection connection = null;

        String drivers = "com.mysql.jdbc.Driver";
        Class.forName(drivers);

        String url = "jdbc:mysql://localhost:3306/beike?useUnicode=true&characterEncoding=UTF-8&allowMultiQueries=true";
        String username = "root";
        String password = "Xande@123z456P;";

        connection = DriverManager.getConnection(url,username, password);

        return connection;
    }

    /**
     * 执行更新
     * @param sqlList
     * @throws SQLException
     * @throws ClassNotFoundException
     * @throws InterruptedException
     */
    public static void updateMemberDistribution(List<String> sqlList) throws SQLException, ClassNotFoundException {

        Connection connection = getConnection();
        //关闭自动提交，即开启事务
        connection.setAutoCommit(false);
        Statement stmt = connection.createStatement();

        for (String sql : sqlList) {
            stmt.addBatch(sql);
        }

        stmt.executeBatch();
        //提交事务
        connection.commit();
        stmt.close();
        connection.setAutoCommit(true);
        connection.close();
    }

}
