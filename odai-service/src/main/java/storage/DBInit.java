package storage;

import java.sql.Connection;
import java.sql.Statement;

public class DBInit {

    public static void init() {
        try (Connection con = DBUtil.getConnection();
             Statement st = con.createStatement()) {

            st.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    user_id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(100) NOT NULL UNIQUE,
                    password_hash VARCHAR(255) NOT NULL,
                    discord_webhook_url VARCHAR(1000),
                    discord_user_id VARCHAR(50)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS questions (
                    id VARCHAR(20) PRIMARY KEY,
                    title VARCHAR(255) NOT NULL,
                    limit_count INT NOT NULL,
                    owner_user_id INT,
                    FOREIGN KEY (owner_user_id) REFERENCES users(user_id)
                )
            """);

            st.execute("""
                CREATE TABLE IF NOT EXISTS answers (
                    answer_id INT AUTO_INCREMENT PRIMARY KEY,
                    question_id VARCHAR(20) NOT NULL,
                    text CLOB NOT NULL,
                    favorite BOOLEAN DEFAULT FALSE,
                    read_flag BOOLEAN DEFAULT FALSE,
                    special_type VARCHAR(30) DEFAULT '',
                    final_flag BOOLEAN DEFAULT FALSE,
                    final_rank INT DEFAULT 0,
                    FOREIGN KEY (question_id) REFERENCES questions(id) ON DELETE CASCADE
                )
            """);

            addColumnIfNotExists(st,
                    "ALTER TABLE users ADD COLUMN discord_webhook_url VARCHAR(1000)");
            addColumnIfNotExists(st,
                    "ALTER TABLE users ADD COLUMN discord_user_id VARCHAR(50)");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void addColumnIfNotExists(Statement st, String sql) {
        try {
            st.execute(sql);
        } catch (Exception e) {
            // 既にあるなら無視
        }
    }
}