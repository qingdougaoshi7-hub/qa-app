package storage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.User;

public class UserDAO {

    public boolean existsByUsername(String username) throws Exception {
        String sql = "SELECT COUNT(*) FROM users WHERE username = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }

        return false;
    }

    public void insert(String username, String passwordHash, String discordWebhookUrl) throws Exception {
        String sql =
                "INSERT INTO users " +
                "(username, password_hash, discord_webhook_url, discord_user_id, reset_keyword_hash) " +
                "VALUES (?, ?, ?, NULL, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, emptyToNull(discordWebhookUrl));
            ps.setString(4, "");
            ps.executeUpdate();
        }
    }

    public void insertUser(String username, String passwordHash) throws Exception {
        String sql =
                "INSERT INTO users " +
                "(username, password_hash, discord_webhook_url, discord_user_id, reset_keyword_hash) " +
                "VALUES (?, ?, NULL, NULL, ?)";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, "");
            ps.executeUpdate();
        }
    }

    public User authenticate(String username, String passwordHash) throws Exception {
        String sql =
                "SELECT user_id, username, password_hash, discord_webhook_url, discord_user_id " +
                "FROM users " +
                "WHERE username = ? AND password_hash = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, passwordHash);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    public User findByUsername(String username) throws Exception {
        String sql =
                "SELECT user_id, username, password_hash, discord_webhook_url, discord_user_id " +
                "FROM users " +
                "WHERE username = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, username);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    public User findById(int userId) throws Exception {
        String sql =
                "SELECT user_id, username, password_hash, discord_webhook_url, discord_user_id " +
                "FROM users " +
                "WHERE user_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapUser(rs);
                }
            }
        }

        return null;
    }

    public void updatePassword(int userId, String newPasswordHash) throws Exception {
        String sql = "UPDATE users SET password_hash = ? WHERE user_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setInt(2, userId);
            ps.executeUpdate();
        }
    }

    public boolean resetPassword(String username, String newPasswordHash, String discordWebhookUrl) throws Exception {
        String sql =
                "UPDATE users " +
                "SET password_hash = ? " +
                "WHERE username = ? " +
                "AND ( " +
                "      (discord_webhook_url = ?) " +
                "      OR (discord_webhook_url IS NULL AND (? IS NULL OR ? = '')) " +
                "    )";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, newPasswordHash);
            ps.setString(2, username);
            ps.setString(3, emptyToNull(discordWebhookUrl));
            ps.setString(4, discordWebhookUrl);
            ps.setString(5, discordWebhookUrl);

            return ps.executeUpdate() > 0;
        }
    }

    public void updateDiscordSettings(int userId, String webhookUrl, String discordUserId) throws Exception {
        String sql =
                "UPDATE users " +
                "SET discord_webhook_url = ?, discord_user_id = ? " +
                "WHERE user_id = ?";

        try (Connection con = DBUtil.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, emptyToNull(webhookUrl));
            ps.setString(2, emptyToNull(discordUserId));
            ps.setInt(3, userId);
            ps.executeUpdate();
        }
    }

    private String emptyToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private User mapUser(ResultSet rs) throws Exception {
        return new User(
                rs.getInt("user_id"),
                rs.getString("username"),
                rs.getString("password_hash"),
                rs.getString("discord_webhook_url"),
                rs.getString("discord_user_id")
        );
    }
}