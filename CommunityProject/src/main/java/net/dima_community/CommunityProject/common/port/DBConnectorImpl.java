package net.dima_community.CommunityProject.common.port;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.context.annotation.Configuration;

import net.dima_community.CommunityProject.common.infra.DBConnector;

@Configuration
public class DBConnectorImpl implements DBConnector {

    private String driver = "com.mysql.jdbc.Driver";
    private String url = "jdbc:mysql://localhost:3306/test_db";
    private String id = "root";
    private String pwd = "0000";

    @Override
    public Connection getConnection() {
        Connection conn = null;

        try {
            // JDBC 드라이버 로딩
            Class.forName(driver);

            // Connection 생성
            conn = DriverManager.getConnection(url, id, pwd);

            return conn;

        } catch (SQLException e) {

            System.out.println("[SQL Error : " + e.getMessage() + "]");
            return null;

        } catch (ClassNotFoundException e1) {

            System.out.println("[JDBC Connector Driver Error : " + e1.getMessage() + "]");
            return null;
        }

    }
}