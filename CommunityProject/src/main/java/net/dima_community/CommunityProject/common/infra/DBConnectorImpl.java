package net.dima_community.CommunityProject.common.infra;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import net.dima_community.CommunityProject.common.port.DBConnector;

@Configuration
public class DBConnectorImpl implements DBConnector {

    @Value("${spring.datasource.driver-class-name}")
    private String driver;
    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String id;
    @Value("${spring.datasource.password}")
    private String pwd;

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
