package storage;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("PostgreSQLドライバの読み込みに失敗しました", e);
        }
    }

    private static String getJdbcUrl() {
        String databaseUrl = System.getenv("DATABASE_URL");

        if (databaseUrl == null || databaseUrl.isBlank()) {
            throw new RuntimeException("環境変数 DATABASE_URL が設定されていません。");
        }

        if (databaseUrl.startsWith("jdbc:postgresql://")) {
            return databaseUrl;
        }

        if (databaseUrl.startsWith("postgresql://")) {
            return "jdbc:" + databaseUrl;
        }

        if (databaseUrl.startsWith("postgres://")) {
            return "jdbc:postgresql://" + databaseUrl.substring("postgres://".length());
        }

        throw new RuntimeException("DATABASE_URL の形式が不正です: " + databaseUrl);
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(getJdbcUrl());
    }
}