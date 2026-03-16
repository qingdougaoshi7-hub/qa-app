<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Question" %>
<%@ page import="model.Answer" %>

<%
    Question q = (Question) request.getAttribute("question");
    List<Answer> filteredAnswers = (List<Answer>) request.getAttribute("filteredAnswers");
    List<Answer> ranking = (List<Answer>) request.getAttribute("ranking");
    String keyword = (String) request.getAttribute("keyword");
    String mode = (String) request.getAttribute("mode");
    Double answerRate = (Double) request.getAttribute("answerRate");
    Integer allAnswerCount = (Integer) request.getAttribute("allAnswerCount");
    Integer currentPage = (Integer) request.getAttribute("currentPage");
    Integer totalPages = (Integer) request.getAttribute("totalPages");

    if (keyword == null) keyword = "";
    if (mode == null) mode = "";
    if (answerRate == null) answerRate = 0.0;
    if (allAnswerCount == null) allAnswerCount = 0;
    if (currentPage == null) currentPage = 1;
    if (totalPages == null) totalPages = 1;
    if (filteredAnswers == null) filteredAnswers = java.util.Collections.emptyList();
    if (ranking == null) ranking = java.util.Collections.emptyList();

    String origin = request.getRequestURL().toString().replace(request.getRequestURI(), "");
    String baseUrl = origin + request.getContextPath();

    String answerUrl = baseUrl + "/q/" + q.getId();
    String manageUrl = baseUrl + "/manage/" + q.getId();

    boolean isAnswerListMode = !"favorites".equals(mode) && !"special".equals(mode) && !"final".equals(mode);
    boolean isFavoritesMode = "favorites".equals(mode);
    boolean isSpecialMode = "special".equals(mode);
    boolean isFinalMode = "final".equals(mode);

    int answeredCount = allAnswerCount;
    int limit = q.getLimit();
    String limitText = (limit == -1) ? "無制限" : String.valueOf(limit);
%>
<!DOCTYPE html>
<html lang="ja">
<head>
    <meta charset="UTF-8">
    <title>管理ページ - <%= q.getTitle() %></title>
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="<%= request.getContextPath() %>/css/style.css">
</head>
<body>
<jsp:include page="/WEB-INF/jsp/common/header.jspf" />

<div class="page-container manage-shell">
    <aside class="manage-sidebar card">
        <div class="manage-sidebar-sticky">
            <div class="section-kicker">管理メニュー</div>
            <h2 class="manage-sidebar-title"><%= q.getTitle() %></h2>

            <div class="manage-sidebar-links">
                <a class="manage-side-link <%= isAnswerListMode ? "active" : "" %>"
                   href="<%= request.getContextPath() %>/manage/<%= q.getId() %>">
                    回答一覧
                </a>

                <a class="manage-side-link <%= isFavoritesMode ? "active" : "" %>"
                   href="<%= request.getContextPath() %>/manage/<%= q.getId() %>?mode=favorites">
                    お気に入り
                </a>

                <a class="manage-side-link <%= isSpecialMode ? "active" : "" %>"
                   href="<%= request.getContextPath() %>/manage/<%= q.getId() %>?mode=special">
                    特別枠
                </a>

                <a class="manage-side-link <%= isFinalMode ? "active" : "" %>"
                   href="<%= request.getContextPath() %>/manage/<%= q.getId() %>?mode=final">
                    決勝
                </a>

                <a class="manage-side-link" href="#rankingArea">
                    順位一覧
                </a>

                <a class="manage-side-link" href="<%= answerUrl %>" target="_blank" rel="noopener noreferrer">
                    回答ページを開く
                </a>
            </div>

            <div class="manage-side-stats">
                <div class="manage-side-stat">
                    <strong>回答数</strong>
                    <span><%= answeredCount %> 件</span>
                </div>
                <div class="manage-side-stat">
                    <strong>上限</strong>
                    <span><%= limitText %></span>
                </div>
                <div class="manage-side-stat">
                    <strong>回答率</strong>
                    <span><%= String.format("%.1f", answerRate) %>%</span>
                </div>
            </div>

            <div class="manage-side-urls">
                <div class="url-card">
                    <strong>回答URL</strong>
                    <a href="<%= answerUrl %>" target="_blank" rel="noopener noreferrer"><%= answerUrl %></a>
                </div>
                <div class="url-card">
                    <strong>管理URL</strong>
                    <a href="<%= manageUrl %>" target="_blank" rel="noopener noreferrer"><%= manageUrl %></a>
                </div>
            </div>
        </div>
    </aside>

    <main class="manage-main">
        <section class="card">
            <div class="page-title-row">
                <div>
                    <div class="section-kicker">管理ページ</div>
                    <h1><%= q.getTitle() %></h1>
                    <p class="lead-text">
                        回答の確認、検索、お気に入り、特別枠、決勝、順位付けができます。
                    </p>
                </div>
            </div>

            <form class="global-search-form" method="get" action="<%= request.getContextPath() %>/manage/<%= q.getId() %>">
                <input type="hidden" name="mode" value="<%= mode %>">
                <input type="text" name="keyword" value="<%= keyword %>" placeholder="回答文を検索">
                <button type="submit" class="main-button">検索</button>
                <% if (!keyword.isBlank()) { %>
                    <a class="sub-button" href="<%= request.getContextPath() %>/manage/<%= q.getId() %><%= mode.isBlank() ? "" : "?mode=" + mode %>">クリア</a>
                <% } %>
            </form>
        </section>

        <section class="answer-list-card">
            <div class="page-title-row">
                <div>
                    <div class="section-kicker">
                        <%= isFavoritesMode ? "お気に入り" : isSpecialMode ? "特別枠" : isFinalMode ? "決勝" : "回答一覧" %>
                    </div>
                    <h2>
                        <%= isFavoritesMode ? "お気に入り回答" : isSpecialMode ? "特別枠の回答" : isFinalMode ? "決勝候補の回答" : "すべての回答" %>
                    </h2>
                </div>
            </div>

            <div class="answer-list">
                <% if (filteredAnswers.isEmpty()) { %>
                    <div class="empty-state">
                        <strong>まだ表示できる回答がありません。</strong>
                        条件に合う回答が見つかりませんでした。
                    </div>
                <% } %>

                <% for (Answer a : filteredAnswers) {
                    String specialType = "";
                    try {
                        specialType = a.getSpecialType();
                    } catch (Exception ignore) {
                    }

                    boolean favorite = false;
                    boolean finalFlag = false;
                    int finalRank = 0;

                    try { favorite = a.isFavorite(); } catch (Exception ignore) {}
                    try { finalFlag = a.isFinalFlag(); } catch (Exception ignore) {}
                    try { finalRank = a.getFinalRank(); } catch (Exception ignore) {}

                    boolean special = "special".equals(specialType);
                    boolean chihuahua = "chihuahua".equals(specialType);
                %>
                <article class="answer-card" data-answer-id="<%= a.getAnswerId() %>">
                    <div class="answer-status-row">
                        <% if (favorite) { %>
                            <span class="answer-status-chip favorite">お気に入り</span>
                        <% } %>
                        <% if (special) { %>
                            <span class="answer-status-chip special">特別枠</span>
                        <% } %>
                        <% if (chihuahua) { %>
                            <span class="answer-status-chip chihuahua">チワワ賞</span>
                        <% } %>
                        <% if (finalFlag) { %>
                            <span class="answer-status-chip final">決勝</span>
                        <% } %>
                        <% if (finalRank >= 1 && finalRank <= 5) { %>
                            <span class="ranking-badge"><%= finalRank %>位</span>
                        <% } %>
                    </div>

                    <div class="answer-text"><%= a.getText() %></div>

                    <div class="answer-actions">
                        <button type="button"
                                class="favorite-button <%= favorite ? "active" : "" %>"
                                onclick="toggleFavorite(<%= a.getAnswerId() %>)">
                            <%= favorite ? "お気に入り解除" : "お気に入り" %>
                        </button>

                        <button type="button"
                                class="icon-button"
                                onclick="toggleMarkAction('<%= q.getId() %>', <%= a.getAnswerId() %>, 'special', <%= currentPage %>)">
                            <%= special ? "特別枠を解除" : "特別枠にする" %>
                        </button>

                        <button type="button"
                                class="icon-button"
                                onclick="toggleMarkAction('<%= q.getId() %>', <%= a.getAnswerId() %>, 'chihuahua', <%= currentPage %>)">
                            <%= chihuahua ? "チワワ賞を解除" : "チワワ賞にする" %>
                        </button>

                        <% if (isFavoritesMode) { %>
                            <button type="button"
                                    class="icon-button"
                                    onclick="toggleMarkAction('<%= q.getId() %>', <%= a.getAnswerId() %>, 'final', <%= currentPage %>)">
                                <%= finalFlag ? "決勝を解除" : "このページの決勝にする" %>
                            </button>
                        <% } %>

                        <% if (isFinalMode) { %>
                            <form method="post" action="<%= request.getContextPath() %>/rank" class="rank-form-inline">
                                <input type="hidden" name="questionId" value="<%= q.getId() %>">
                                <input type="hidden" name="answerId" value="<%= a.getAnswerId() %>">
                                <select name="rank">
                                    <option value="1" <%= finalRank == 1 ? "selected" : "" %>>1位</option>
                                    <option value="2" <%= finalRank == 2 ? "selected" : "" %>>2位</option>
                                    <option value="3" <%= finalRank == 3 ? "selected" : "" %>>3位</option>
                                    <option value="4" <%= finalRank == 4 ? "selected" : "" %>>4位</option>
                                    <option value="5" <%= finalRank == 5 ? "selected" : "" %>>5位</option>
                                </select>
                                <button type="submit" class="main-button">順位を保存</button>
                            </form>
                        <% } %>
                    </div>
                </article>
                <% } %>
            </div>

            <% if (totalPages > 1) { %>
                <div class="pagination">
                    <% for (int p = 1; p <= totalPages; p++) { %>
                        <a class="tab-button <%= p == currentPage ? "active" : "" %>"
                           href="<%= request.getContextPath() %>/manage/<%= q.getId() %>?mode=<%= mode %>&keyword=<%= java.net.URLEncoder.encode(keyword, "UTF-8") %>&page=<%= p %>">
                            <%= p %>
                        </a>
                    <% } %>
                </div>
            <% } %>
        </section>

        <section class="answer-list-card" id="rankingArea">
            <div class="page-title-row">
                <div>
                    <div class="section-kicker">順位一覧</div>
                    <h2>現在の順位</h2>
                </div>
            </div>

            <div class="answer-list">
                <% if (ranking.isEmpty()) { %>
                    <div class="empty-state">
                        <strong>まだ順位は決まっていません。</strong>
                        決勝ページで順位を付けてください。
                    </div>
                <% } %>

                <% for (Answer r : ranking) { %>
                    <article class="answer-card ranking-card">
                        <div class="ranking-title"><%= r.getFinalRank() %>位</div>
                        <div class="ranking-answer-text"><%= r.getText() %></div>
                    </article>
                <% } %>
            </div>
        </section>
    </main>
</div>

<script>
function toggleFavorite(answerId) {
    fetch("<%= request.getContextPath() %>/favorite", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
        body: "answerId=" + encodeURIComponent(answerId)
    })
    .then(function (res) {
        if (!res.ok) throw new Error();
        location.reload();
    })
    .catch(function () {
        alert("お気に入りの更新に失敗しました。");
    });
}

function toggleMarkAction(questionId, answerId, action, page) {
    fetch("<%= request.getContextPath() %>/mark-answer", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8" },
        body:
            "questionId=" + encodeURIComponent(questionId) +
            "&answerId=" + encodeURIComponent(answerId) +
            "&action=" + encodeURIComponent(action) +
            "&page=" + encodeURIComponent(page)
    })
    .then(function (res) {
        if (!res.ok) throw new Error();
        location.reload();
    })
    .catch(function () {
        alert("更新に失敗しました。");
    });
}
</script>

<script src="<%= request.getContextPath() %>/js/answer-image.js"></script>
</body>
</html>