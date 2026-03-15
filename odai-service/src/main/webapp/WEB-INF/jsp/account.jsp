<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    User loginUser = (User) request.getAttribute("loginUser");
    if (loginUser == null) {
        loginUser = (User) session.getAttribute("loginUser");
    }

    String updated = request.getParameter("updated");
    String claimed = request.getParameter("claimed");
    String claimedCount = request.getParameter("count");
    String errorMessage = (String) request.getAttribute("errorMessage");

    String webhookUrl = "";
    String discordUserId = "";

    if (loginUser != null) {
        if (loginUser.getDiscordWebhookUrl() != null) {
            webhookUrl = loginUser.getDiscordWebhookUrl();
        }
        if (loginUser.getDiscordUserId() != null) {
            discordUserId = loginUser.getDiscordUserId();
        }
    }

    if (claimedCount == null) {
        claimedCount = "0";
    }
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>通知設定</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.account-card {
    max-width: 760px;
    margin: 0 auto;
}

.account-form {
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.account-field label {
    display: block;
    font-weight: bold;
    margin-bottom: 8px;
}

.account-field input {
    width: 100%;
    min-height: 48px;
    padding: 12px 14px;
    border: 1px solid #d9c9a5;
    border-radius: 14px;
    font-size: 16px;
    box-sizing: border-box;
}

.account-help {
    font-size: 13px;
    color: #5f5646;
    line-height: 1.8;
    margin-top: 6px;
}

.account-success {
    background: #eef9ee;
    color: #205b20;
    border: 1px solid #b7dfb7;
    padding: 12px 14px;
    border-radius: 14px;
    margin-bottom: 18px;
}

.account-error {
    background: #fff0f0;
    color: #8a2222;
    border: 1px solid #efb8b8;
    padding: 12px 14px;
    border-radius: 14px;
    margin-bottom: 18px;
}

.account-save-button,
.account-claim-button {
    min-height: 52px;
    border: none;
    border-radius: 16px;
    background: #1f1f1f;
    color: #fff;
    font-size: 16px;
    font-weight: bold;
    cursor: pointer;
    padding: 0 18px;
}

.account-save-button:hover,
.account-claim-button:hover {
    opacity: 0.92;
}

.account-sub-card {
    margin-top: 20px;
    padding: 20px;
    border: 1px solid #eadfca;
    border-radius: 18px;
    background: #fffdf8;
}

.account-sub-card h2 {
    margin-top: 0;
    margin-bottom: 10px;
}
</style>
</head>
<body>

<%@ include file="/WEB-INF/jsp/common/header.jspf" %>

<div class="page-container">
    <div class="card account-card">
        <h1>Discord通知設定</h1>
        <p>回答が来たときに、Discord の webhook 先へ通知します。</p>

        <% if ("1".equals(updated)) { %>
            <div class="account-success">通知設定を保存しました。</div>
        <% } %>

        <% if ("1".equals(claimed)) { %>
            <div class="account-success">昔作ったお題を <%= claimedCount %> 件、このアカウントに紐付けました。</div>
        <% } %>

        <% if (errorMessage != null && !errorMessage.isEmpty()) { %>
            <div class="account-error"><%= errorMessage %></div>
        <% } %>

        <form action="${pageContext.request.contextPath}/account" method="post" class="account-form">
            <div class="account-field">
                <label for="discordWebhookUrl">Discord Webhook URL</label>
                <input
                    type="text"
                    id="discordWebhookUrl"
                    name="discordWebhookUrl"
                    value="<%= webhookUrl %>"
                    placeholder="https://discord.com/api/webhooks/..."
                >
                <div class="account-help">
                    Discord の通知を送る先の Webhook URL を入れてください。
                </div>
            </div>

            <div class="account-field">
                <label for="discordUserId">通知メンション用 Discord ユーザーID</label>
                <input
                    type="text"
                    id="discordUserId"
                    name="discordUserId"
                    value="<%= discordUserId %>"
                    placeholder="123456789012345678"
                >
                <div class="account-help">
                    通知時にこの ID のユーザーを直接メンションします。<br>
                    Discord の開発者モードを ON にして、自分のプロフィールを右クリック → 「IDをコピー」で取得できます。
                </div>
            </div>

            <button type="submit" class="account-save-button">保存する</button>
        </form>

        <div class="account-sub-card">
            <h2>昔作ったお題を引き継ぐ</h2>
            <p>
                以前作ったお題に作成者情報が入っていない場合、通知が飛ばないことがあります。<br>
                下のボタンで、作成者未設定のお題をこのアカウントにまとめて紐付けできます。
            </p>

            <form action="${pageContext.request.contextPath}/claim-legacy-questions" method="post">
                <button type="submit" class="account-claim-button">昔のお題をこのアカウントに紐付ける</button>
            </form>
        </div>
    </div>
</div>

</body>
</html>