<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Question" %>
<%@ page import="model.Answer" %>
<%
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    Question q = (Question) request.getAttribute("question");
    List<Answer> ranking = (List<Answer>) request.getAttribute("ranking");
    Answer chihuahuaAward = (Answer) request.getAttribute("chihuahuaAward");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>順位表</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>

<div class="page-container">
<% if (q == null) { %>
    <div class="search-card">
        <div class="section-kicker">順位表</div>
        <h1>お題を選んで順位表を見る</h1>
        <p class="lead-text">公開用の見やすい順位表ページです。見たいお題を下から選んでください。</p>
    </div>

    <div class="list-card">
        <h2>お題一覧</h2>
        <div class="question-list">
            <% if (questions != null && !questions.isEmpty()) {
                for (Question item : questions) { %>
            <div class="question-item">
                <a href="<%= request.getContextPath() %>/ranking/<%= item.getId() %>" class="question-link">
                    <span class="question-title"><%= item.getTitle() %></span>
                    <span class="question-meta">このお題の順位表を見る</span>
                    <div class="meta-pills">
                        <span class="meta-pill">回答数 <%= item.getAnswerCount() %></span>
                        <span class="meta-pill">ID <%= item.getId() %></span>
                    </div>
                </a>
            </div>
            <% }
            } else { %>
            <div class="question-item">
                <div class="question-link empty-state">
                    <strong>お題がありません</strong>
                    <span>お題を作成すると、ここに順位表ページが並びます。</span>
                </div>
            </div>
            <% } %>
        </div>
    </div>
<% } else { %>
    <div class="card">
        <div class="section-kicker">順位表</div>
        <h1><%= q.getTitle() %></h1>
        <p class="lead-text">このお題の決勝結果とチワワ賞をまとめて表示しています。</p>
    </div>

    <div class="answer-list-card">
        <h2>順位結果</h2>
        <div class="answer-list">
            <% if (ranking != null && !ranking.isEmpty()) {
                for (Answer a : ranking) { %>
            <div class="answer-card ranking-card">
                <div class="ranking-title"><%= a.getFinalRank() %>位</div>
                <div class="ranking-answer-text"><%= a.getText() %></div>
            </div>
            <% }
            } else { %>
            <div class="answer-card ranking-card">
                <div class="ranking-answer-text">まだ順位は決まっていません。</div>
            </div>
            <% } %>

            <% if (chihuahuaAward != null) { %>
            <div class="answer-card ranking-card">
                <div class="ranking-title">チワワ賞</div>
                <div class="ranking-answer-text"><%= chihuahuaAward.getText() %></div>
            </div>
            <% } %>
        </div>
    </div>
<% } %>
</div>
</body>
</html>
