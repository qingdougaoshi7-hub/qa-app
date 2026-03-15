package storage;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.nio.charset.StandardCharsets;

public class DiscordNotifier {

    public static void sendNewAnswer(
            String webhookUrl,
            String discordUserId,
            String questionTitle,
            String answerText,
            String manageUrl
    ) throws Exception {

        if (webhookUrl == null || webhookUrl.isBlank()) {
            throw new IllegalArgumentException("Webhook URL が未設定です。");
        }

        String trimmedWebhookUrl = webhookUrl.trim();
        String normalizedUserId = normalizeDiscordUserId(discordUserId);

        String content;
        String payload;

        if (normalizedUserId.isEmpty()) {
            content =
                    "📩 新しい回答が届きました\n" +
                    "お題: " + safe(questionTitle) + "\n" +
                    "回答: " + safe(answerText) + "\n" +
                    "管理URL: " + safe(manageUrl);

            payload =
                    "{"
                    + "\"content\":\"" + escapeJson(content) + "\""
                    + "}";
        } else {
            content =
                    "<@" + normalizedUserId + ">\n" +
                    "📩 新しい回答が届きました\n" +
                    "お題: " + safe(questionTitle) + "\n" +
                    "回答: " + safe(answerText) + "\n" +
                    "管理URL: " + safe(manageUrl);

            payload =
                    "{"
                    + "\"content\":\"" + escapeJson(content) + "\","
                    + "\"allowed_mentions\":{"
                    + "\"parse\":[],"
                    + "\"users\":[\"" + escapeJson(normalizedUserId) + "\"]"
                    + "}"
                    + "}";
        }

        HttpURLConnection con =
                (HttpURLConnection) URI.create(trimmedWebhookUrl).toURL().openConnection();

        con.setRequestMethod("POST");
        con.setDoOutput(true);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = con.getOutputStream()) {
            os.write(payload.getBytes(StandardCharsets.UTF_8));
        }

        int status = con.getResponseCode();

        if (status < 200 || status >= 300) {
            String errorBody = readBody(con, true);
            throw new RuntimeException("Discord通知失敗: HTTP " + status + " / " + errorBody);
        }

        con.disconnect();
    }

    private static String normalizeDiscordUserId(String discordUserId) {
        if (discordUserId == null) {
            return "";
        }

        String normalized = discordUserId.trim().replaceAll("[^0-9]", "");

        if (normalized.length() < 17 || normalized.length() > 20) {
            return "";
        }

        return normalized;
    }

    private static String readBody(HttpURLConnection con, boolean error) {
        try (InputStream is = error ? con.getErrorStream() : con.getInputStream()) {
            if (is == null) {
                return "";
            }
            byte[] bytes = is.readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return "";
        }
    }

    private static String safe(String value) {
        return value == null ? "" : value;
    }

    private static String escapeJson(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "");
    }
}