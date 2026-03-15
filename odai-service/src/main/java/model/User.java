package model;

public class User {
    private int userId;
    private String username;
    private String passwordHash;
    private String discordWebhookUrl;
    private String discordUserId;

    public User() {
    }

    public User(int userId, String username, String passwordHash, String discordWebhookUrl, String discordUserId) {
        this.userId = userId;
        this.username = username;
        this.passwordHash = passwordHash;
        this.discordWebhookUrl = discordWebhookUrl;
        this.discordUserId = discordUserId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getDiscordWebhookUrl() {
        return discordWebhookUrl;
    }

    public void setDiscordWebhookUrl(String discordWebhookUrl) {
        this.discordWebhookUrl = discordWebhookUrl;
    }

    public String getDiscordUserId() {
        return discordUserId;
    }

    public void setDiscordUserId(String discordUserId) {
        this.discordUserId = discordUserId;
    }
}