<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Question" %>
<%
    Question q = (Question) request.getAttribute("question");
%>
<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title>お題作成</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
</head>
<body>
<%@ include file="/WEB-INF/jsp/common/header.jspf" %>
<div class="page-container">
    <div class="card">
        <h1>お題作成</h1>
        <form action="${pageContext.request.contextPath}/create" method="post" class="form-block">
            <label for="title">お題タイトル</label>
            <input id="title" type="text" name="title" class="text-input" required>

            <label for="limit">回答数制限</label>
            <input id="limit" type="number" name="limit" class="text-input" min="1">

            <label class="check-line">
                <input type="checkbox" name="unlimit">
                無制限にする
            </label>

            <button type="submit" class="main-button">作成</button>
        </form>

        <% if (q != null) { %>
        <hr>
        <div class="result-box">
            <p><strong>作成完了</strong></p>
            <p>
                回答URL<br>
                <a href="<%=request.getContextPath()%>/q/<%=q.getId()%>">http://localhost:8080<%=request.getContextPath()%>/q/<%=q.getId()%></a>
            </p>
            <p>
                管理URL<br>
                <a href="<%=request.getContextPath()%>/manage/<%=q.getId()%>">http://localhost:8080<%=request.getContextPath()%>/manage/<%=q.getId()%></a>
            </p>
        </div>
        <% } %>
    </div>
</div>
</body>
</html>
