package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    private static final String USER =
            System.getenv().getOrDefault("JDBC_USER", "sa");

    private static final String PASSWORD =
            System.getenv().getOrDefault("JDBC_PASSWORD", "");

    static {
        try {
            Class.forName("org.h2.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("H2ドライバの読み込みに失敗しました", e);
        }
    }

    private static String getJdbcUrl() {
        String envUrl = System.getenv("JDBC_URL");
        if (envUrl != null && !envUrl.isBlank()) {
            return envUrl;
        }

        String render = System.getenv("RENDER");
        if (render != null && !render.isBlank()) {
            return "jdbc:h2:file:/tmp/odai_service_db3;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE";
        }

        return "jdbc:h2:~/odai_service_db3;AUTO_SERVER=TRUE";
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl(), USER, PASSWORD);
    }
}