package storage;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

public class DBUtil {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQLドライバの読み込みに失敗しました", e);
        }
    }

    public static Connection getConnection() throws SQLException {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new RuntimeException("環境変数 DATABASE_URL が設定されていません。");
        }

        try {
            URI uri = new URI(databaseUrl);

            String scheme = uri.getScheme();
            if (!"postgres".equals(scheme) && !"postgresql".equals(scheme)) {
                throw new RuntimeException("DATABASE_URL の形式が不正です: " + databaseUrl);
            }

            String host = uri.getHost();
            int port = uri.getPort();
            String databaseName = uri.getPath().substring(1);

            String jdbcUrl;
            if (port > 0) {
                jdbcUrl = "jdbc:postgresql://" + host + ":" + port + "/" + databaseName;
            } else {
                jdbcUrl = "jdbc:postgresql://" + host + "/" + databaseName;
            }

            Properties props = new Properties();

            String userInfo = uri.getUserInfo();
            if (userInfo != null) {
                String[] parts = userInfo.split(":", 2);
                if (parts.length > 0) {
                    props.setProperty("user", parts[0]);
                }
                if (parts.length > 1) {
                    props.setProperty("password", parts[1]);
                }
            }

            return DriverManager.getConnection(jdbcUrl, props);

        } catch (Exception e) {
            throw new RuntimeException("DATABASE_URL の解析に失敗しました: " + databaseUrl, e);
        }
    }
}