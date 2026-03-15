<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%
    String errorMessage = (String) request.getAttribute("errorMessage");
    String username = (String) request.getAttribute("username");
    String reset = request.getParameter("reset");

    if (username == null) {
        username = "";
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>ログイン</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.auth-card {
    max-width: 560px;
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
.auth-success {
    background: #eef9ee;
    color: #205b20;
    border: 1px solid #b7dfb7;
    padding: 12px 14px;
    border-radius: 14px;
    margin-bottom: 18px;
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
        <h1>ログイン</h1>
        <p>登録したユーザー名とパスワードでログインできます。</p>

        <% if ("1".equals(reset)) { %>
            <div class="auth-success">パスワードを再設定しました。新しいパスワードでログインしてください。</div>
        <% } %>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="auth-error"><%= errorMessage %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/login" method="post" class="auth-form">
            <div class="auth-field">
                <label for="username">ユーザー名</label>
                <input type="text" id="username" name="username" value="<%= username %>" required>
            </div>

            <div class="auth-field">
                <label for="password">パスワード</label>
                <input type="password" id="password" name="password" required>
            </div>

            <button type="submit" class="auth-button">ログインする</button>

            <div class="auth-links">
                <a href="${pageContext.request.contextPath}/register">新規登録</a>
                <a href="${pageContext.request.contextPath}/reset-password">パスワードを忘れたとき</a>
            </div>
        </form>
    </div>
</div>

</body>
</html>