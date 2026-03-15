<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Question" %>
<%
    List<Question> questions = (List<Question>) request.getAttribute("questions");
    String keyword = (String) request.getAttribute("keyword");
    if (keyword == null) keyword = "";
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>自分のお題</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-container">
    <div class="search-card">
        <form action="${pageContext.request.contextPath}/questions" method="get" class="global-search-form">
            <input type="text" name="keyword" placeholder="自分のお題を検索" value="<%= keyword %>">
            <button type="submit">検索</button>
        </form>
    </div>

    <div class="list-card">
        <h2>自分が作成したお題</h2>
        <div class="question-list">
            <% if (questions != null && !questions.isEmpty()) {
                   for (Question q : questions) { %>
                <div class="question-item">
                    <a href="<%=request.getContextPath()%>/manage/<%=q.getId()%>" class="question-link">
                        <span class="question-title"><%= q.getTitle() %></span>
                        <span class="question-meta">回答数: <%= q.getAnswerCount() %></span>
                    </a>
                </div>
            <%   }
               } else { %>
                <div class="question-item">
                    <div class="question-link">
                        <span class="question-title">まだお題がありません</span>
                        <span class="question-meta">まずはお題を作成してください</span>
                    </div>
                </div>
            <% } %>
        </div>
    </div>
</div>
</body>
</html>
