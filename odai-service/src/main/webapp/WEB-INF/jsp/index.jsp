<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="model.User" %>
<%
    User loginUser = (User) session.getAttribute("loginUser");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>合法的留年チワワ お題サービス</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>

<%@ include file="/WEB-INF/jsp/common/header.jspf" %>

<main class="top-main">
    <section class="hero-card">
        <div class="hero-image">
            <img src="${pageContext.request.contextPath}/img/chihuahua_4x.png" alt="合法的留年チワワ">
        </div>
        <div class="hero-text">
            <h2>匿名回答を集めて、管理画面と順位表でまとめよう</h2>
            <p>アカウント登録すると、お題作成、回答管理、Discord通知が使えます。</p>
            <div class="top-buttons">
                <% if (loginUser != null) { %>
                    <a class="main-button" href="${pageContext.request.contextPath}/create">お題作成</a>
                    <a class="sub-button" href="${pageContext.request.contextPath}/questions">自分のお題</a>
                    <a class="sub-button" href="${pageContext.request.contextPath}/account">通知設定</a>
                <% } else { %>
                    <a class="main-button" href="${pageContext.request.contextPath}/register">新規登録</a>
                    <a class="sub-button" href="${pageContext.request.contextPath}/login">ログイン</a>
                    <a class="sub-button" href="${pageContext.request.contextPath}/ranking">順位表</a>
                <% } %>
            </div>
        </div>
    </section>
</main>

<footer class="site-footer">© 合法的留年チワワ</footer>

</body>
</html>
