package net.dima_community.CommunityProject.common.infra;

import java.sql.Connection;

public interface DBConnector {

    Connection getConnection();

}
