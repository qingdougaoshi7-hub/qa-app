<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String username = (String) request.getAttribute("username");
    String discordWebhookUrl = (String) request.getAttribute("discordWebhookUrl");

    if (username == null) {
        username = "";
    }
    if (discordWebhookUrl == null) {
        discordWebhookUrl = "";
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>新規登録</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.auth-card {
    max-width: 620px;
    margin: 0 auto;
}
.auth-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
}
.auth-field label {
    display: block;
    font-weight: bold;
    margin-bottom: 8px;
}
.auth-field input {
    width: 100%;
    min-height: 48px;
    padding: 12px 14px;
    border: 1px solid #d9c9a5;
    border-radius: 14px;
    font-size: 16px;
    box-sizing: border-box;
}
.auth-help {
    font-size: 13px;
    color: #5f5646;
    line-height: 1.8;
    margin-top: 6px;
}
.auth-button {
    min-height: 52px;
    border: none;
    border-radius: 16px;
    background: #1f1f1f;
    color: #fff;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
}
.auth-links {
    display: flex;
    flex-wrap: wrap;
    gap: 14px;
    font-size: 14px;
}
.auth-links a {
    color: #1f1f1f;
    font-weight: bold;
    text-decoration: none;
}
.auth-error {
    background: #fff0f0;
    color: #8a2222;
    border: 1px solid #efb8b8;
    padding: 12px 14px;
    border-radius: 14px;
    margin-bottom: 18px;
}
</style>
</head>
<body>

<%@ include file="/WEB-INF/jsp/common/header.jspf" %>

<div class="page-container">
    <div class="card auth-card">
        <h1>新規登録</h1>
        <p>ユーザー名とパスワードを登録できます。パスワードは簡単なものでも使えます。</p>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="auth-error"><%= errorMessage %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/register" method="post" class="auth-form">
            <div class="auth-field">
                <label for="username">ユーザー名</label>
                <input type="text" id="username" name="username" value="<%= username %>" required>
            </div>

            <div class="auth-field">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" required>
                <div class="auth-help">難しいパスワードでなくても大丈夫です。</div>
            </div>

            <div class="auth-field">
                <label for="discordWebhookUrl">Discord Webhook URL（任意）</label>
                <input type="text" id="discordWebhookUrl" name="discordWebhookUrl" value="<%= discordWebhookUrl %>">
                <div class="auth-help">あとから通知設定画面でも変更できます。</div>
            </div>

            <button type="submit" class="auth-button">登録する</button>

            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/login">ログインへ</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>