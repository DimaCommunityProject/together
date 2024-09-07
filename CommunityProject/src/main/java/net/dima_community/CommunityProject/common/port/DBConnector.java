package net.dima_community.CommunityProject.common.port;

import java.sql.Connection;

public interface DBConnector {

    Connection getConnection();

}
