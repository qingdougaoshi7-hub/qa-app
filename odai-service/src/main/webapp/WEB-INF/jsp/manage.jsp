<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="model.Question" %>
<%@ page import="model.Answer" %>

<%!
    private String escapeHtml(String text) {
        if (text == null) return "";
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private String formatAnswerText(String text) {
        if (text == null) return "";

        String safe = escapeHtml(text);
        StringBuilder sb = new StringBuilder();
        int len = safe.length();
        int count = 0;

        for (int i = 0; i < len; i++) {
            char ch = safe.charAt(i);
            sb.append(ch);

            count++;
            if (count == 20 && i < len - 1) {
                sb.append("<br>");
                count = 0;
            }
        }

        return sb.toString();
    }
%>

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

    String answerUrl = request.getContextPath() + "/q/" + q.getId();
    String manageUrl = request.getContextPath() + "/manage/" + q.getId();
    boolean isAnswerListMode = !"favorites".equals(mode) && !"special".equals(mode) && !"final".equals(mode);
    boolean isFavoritesMode = "favorites".equals(mode);
    boolean isSpecialMode = "special".equals(mode);
    boolean isFinalMode = "final".equals(mode);
%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><%= q.getTitle() %> | 合法的留年チワワ</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">
<style>
.answer-card.updating {
    opacity: 0.72;
}

.answer-card {
    position: relative;
}

.answer-top-row {
    display: flex;
    justify-content: space-between;
    align-items: flex-start;
    gap: 10px;
    margin-bottom: 10px;
}

.answer-status-row {
    display: flex;
    gap: 6px;
    flex-wrap: wrap;
    align-items: flex-start;
    margin: 0;
    flex: 1 1 auto;
}

.answer-actions {
    display: flex;
    justify-content: flex-end;
    align-items: flex-start;
    gap: 6px;
    flex-wrap: wrap;
    margin: 0;
    flex: 0 0 auto;
}

.favorite-button {
    transition: transform 0.15s ease, opacity 0.15s ease, box-shadow 0.15s ease, background 0.15s ease;
}

.favorite-button:hover {
    transform: translateY(-1px);
}

.favorite-button:disabled {
    opacity: 0.65;
    cursor: wait;
}

.favorite-button.state-favorite.active {
    background: #b8860b;
    box-shadow: 0 0 0 2px rgba(184, 134, 11, 0.18);
}

.favorite-button.state-special.active {
    background: #8f3fb0;
    box-shadow: 0 0 0 2px rgba(143, 63, 176, 0.18);
}

.favorite-button.state-chihuahua.active {
    background: #d97706;
    box-shadow: 0 0 0 2px rgba(217, 119, 6, 0.20);
}

.favorite-button.state-final.active {
    background: #0f766e;
    box-shadow: 0 0 0 2px rgba(15, 118, 110, 0.18);
}

.answer-status-chip {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    padding: 3px 9px;
    min-height: 28px;
    border-radius: 999px;
    font-size: 11px;
    font-weight: bold;
    line-height: 1.1;
    color: #fff;
    box-sizing: border-box;
    white-space: nowrap;
}

.answer-status-chip.is-hidden {
    display: none;
}

.answer-status-chip.favorite {
    background: #b8860b;
}

.answer-status-chip.special {
    background: #8f3fb0;
}

.answer-status-chip.chihuahua {
    background: #d97706;
}

.answer-status-chip.final {
    background: #0f766e;
}

.answer-actions .favorite-button {
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-height: 28px;
    padding: 3px 10px;
    border-radius: 999px;
    font-size: 11px;
    font-weight: bold;
    line-height: 1.1;
    vertical-align: top;
    box-sizing: border-box;
    white-space: nowrap;
}

.answer-text {
    text-align: center;
    margin-top: 10px;
    line-height: 1.9;
    word-break: break-all;
}

.ranking-title,
.answer-card p,
.card h2 {
    text-align: center;
}

.ranking-title {
    font-weight: bold;
    margin-bottom: 8px;
}

.ranking-answer-text {
    text-align: center;
    line-height: 1.9;
    word-break: break-all;
}

@media (max-width: 768px) {
    .answer-top-row {
        gap: 8px;
        margin-bottom: 8px;
    }

    .answer-status-row,
    .answer-actions {
        gap: 5px;
    }

    .answer-status-chip {
        min-height: 24px;
        padding: 2px 8px;
        font-size: 10px;
    }

    .answer-actions .favorite-button {
        min-height: 24px;
        padding: 2px 8px;
        font-size: 10px;
    }

    .answer-text,
    .ranking-answer-text {
        font-size: 14px;
        line-height: 1.8;
    }
}

@media (max-width: 480px) {
    .answer-top-row {
        flex-direction: column;
        align-items: flex-start;
    }

    .answer-status-row {
        width: 100%;
    }

    .answer-actions {
        width: 100%;
        justify-content: flex-start;
    }

    .answer-status-chip {
        min-height: 22px;
        padding: 2px 7px;
        font-size: 9px;
    }

    .answer-actions .favorite-button {
        min-height: 22px;
        padding: 2px 7px;
        font-size: 9px;
    }

    .answer-text,
    .ranking-answer-text {
        font-size: 13px;
    }
}
</style>
</head>
<body>

<%@ include file="/WEB-INF/jsp/common/header.jspf" %>

<div class="page-container">

    <div class="card">
        <h1><%= q.getTitle() %></h1>
        <p>回答数: <%= allAnswerCount %></p>

        <% if (q.getLimit() == -1) { %>
            <p>回答数制限: 無制限</p>
        <% } else { %>
            <p>回答率: <%= String.format("%.1f", answerRate) %>%</p>
        <% } %>

        <hr>

        <div class="result-box">
            <p><strong>回答URL</strong></p>
            <p>
                <a href="<%= answerUrl %>">http://localhost:8080<%= answerUrl %></a>
            </p>

            <p><strong>管理URL</strong></p>
            <p>
                <a href="<%= manageUrl %>">http://localhost:8080<%= manageUrl %></a>
            </p>
        </div>

        <hr>

        <form action="<%=request.getContextPath()%>/delete-question" method="post"
              onsubmit="return confirm('本当にこのお題を消去しますか？回答もまとめて削除されます。');">
            <input type="hidden" name="questionId" value="<%= q.getId() %>">
            <button type="submit" class="favorite-button">お題を消去</button>
        </form>
    </div>

    <section class="detail-topbar">
        <div class="tab-area">
            <a href="<%=request.getContextPath()%>/manage/<%=q.getId()%>"
               class="tab-button <%= isAnswerListMode ? "active" : "" %>">
               回答一覧
            </a>

            <a href="<%=request.getContextPath()%>/manage/<%=q.getId()%>?mode=favorites"
               class="tab-button <%= isFavoritesMode ? "active" : "" %>">
               お気に入り
            </a>

            <a href="<%=request.getContextPath()%>/manage/<%=q.getId()%>?mode=special"
               class="tab-button <%= isSpecialMode ? "active" : "" %>">
               特別枠
            </a>

            <a href="<%=request.getContextPath()%>/manage/<%=q.getId()%>?mode=final"
               class="tab-button <%= isFinalMode ? "active" : "" %>">
               決勝
            </a>
        </div>

        <div class="local-search-area">
            <form action="<%=request.getContextPath()%>/manage/<%=q.getId()%>" method="get" class="icon-search-form">
                <% if (!mode.isBlank()) { %>
                    <input type="hidden" name="mode" value="<%= mode %>">
                <% } %>
                <input type="text" name="keyword" value="<%= keyword %>" placeholder="このお題内を検索">
                <button type="submit" class="icon-button">🔍</button>
            </form>
        </div>
    </section>

    <% if (isFinalMode) { %>
    <div class="card">
        <h2>順位表</h2>

        <div class="answer-list">
            <% if (ranking != null && !ranking.isEmpty()) {
                for (Answer r : ranking) { %>
                <div class="answer-card">
                    <div class="ranking-title"><%= r.getFinalRank() %>位</div>
                    <div class="ranking-answer-text"><%= formatAnswerText(r.getText()) %></div>
                </div>
            <% }
            } else { %>
                <div class="answer-card">
                    <p>まだ順位は決まっていません。</p>
                </div>
            <% } %>
        </div>
    </div>
    <% } %>

    <section class="answer-list-card" id="answer-list-top">
        <div class="answer-list">
            <% if (filteredAnswers != null && !filteredAnswers.isEmpty()) {
                for (Answer a : filteredAnswers) { %>

                <div class="answer-card"
                     id="answer-card-<%=a.getAnswerId()%>"
                     data-answer-id="<%=a.getAnswerId()%>">

                    <div class="answer-top-row">
                        <div class="answer-status-row">
                            <% if (isAnswerListMode) { %>
                                <span class="answer-status-chip favorite <%= a.isFavorite() ? "" : "is-hidden" %>" data-chip="favorite">お気に入り中</span>
                                <span class="answer-status-chip special <%= a.isSpecialFrame() ? "" : "is-hidden" %>" data-chip="special">特別枠</span>
                                <span class="answer-status-chip chihuahua <%= a.isChihuahuaAward() ? "" : "is-hidden" %>" data-chip="chihuahua">チワワ賞</span>
                                <span class="answer-status-chip final <%= a.isFinalFlag() ? "" : "is-hidden" %>" data-chip="final">決勝進出</span>
                            <% } else if (isFavoritesMode) { %>
                                <span class="answer-status-chip special <%= a.isSpecialFrame() ? "" : "is-hidden" %>" data-chip="special">特別枠</span>
                                <span class="answer-status-chip final <%= a.isFinalFlag() ? "" : "is-hidden" %>" data-chip="final">決勝進出</span>
                            <% } %>
                        </div>

                        <div class="answer-actions">
                            <% if (isAnswerListMode) { %>
                            <button type="button"
                                    class="favorite-button state-favorite <%= a.isFavorite() ? "active" : "" %>"
                                    data-role="favorite-toggle"
                                    data-answer-id="<%=a.getAnswerId()%>"
                                    onclick="toggleFavorite(this)">
                                <span class="button-label"><%= a.isFavorite() ? "★ お気に入り中" : "☆ お気に入りにする" %></span>
                            </button>

                            <button type="button"
                                    class="favorite-button state-special <%= a.isSpecialFrame() ? "active" : "" %>"
                                    data-role="special-toggle"
                                    data-answer-id="<%=a.getAnswerId()%>"
                                    onclick="toggleSpecial(this)">
                                <span class="button-label"><%= a.isSpecialFrame() ? "✓ 特別枠に設定中" : "特別枠にする" %></span>
                            </button>

                            <button type="button"
                                    class="favorite-button state-chihuahua <%= a.isChihuahuaAward() ? "active" : "" %>"
                                    data-role="chihuahua-toggle"
                                    data-answer-id="<%=a.getAnswerId()%>"
                                    onclick="toggleChihuahua(this)">
                                <span class="button-label"><%= a.isChihuahuaAward() ? "🏆 チワワ賞に設定中" : "チワワ賞にする" %></span>
                            </button>
                            <% } %>

                            <% if (isFavoritesMode) { %>
                            <button type="button"
                                    class="favorite-button state-final <%= a.isFinalFlag() ? "active" : "" %>"
                                    data-role="final-toggle"
                                    data-answer-id="<%=a.getAnswerId()%>"
                                    onclick="toggleFinal(this)">
                                <span class="button-label"><%= a.isFinalFlag() ? "✓ 決勝進出中" : "決勝にする" %></span>
                            </button>
                            <% } %>

                            <% if (isFinalMode) { %>
                            <form action="<%=request.getContextPath()%>/rank" method="post" style="display:inline;">
                                <input type="hidden" name="questionId" value="<%= q.getId() %>">
                                <input type="hidden" name="answerId" value="<%= a.getAnswerId() %>">
                                <select name="rank">
                                    <option value="1" <%= a.getFinalRank() == 1 ? "selected" : "" %>>1位</option>
                                    <option value="2" <%= a.getFinalRank() == 2 ? "selected" : "" %>>2位</option>
                                    <option value="3" <%= a.getFinalRank() == 3 ? "selected" : "" %>>3位</option>
                                    <option value="4" <%= a.getFinalRank() == 4 ? "selected" : "" %>>4位</option>
                                    <option value="5" <%= a.getFinalRank() == 5 ? "selected" : "" %>>5位</option>
                                </select>
                                <button type="submit" class="favorite-button">順位登録</button>
                            </form>
                            <% } %>
                        </div>
                    </div>

                    <div class="answer-text"><%= formatAnswerText(a.getText()) %></div>
                </div>

            <% }
            } else { %>
                <div class="answer-card">
                    <p>該当する回答はありません。</p>
                </div>
            <% } %>
        </div>

        <% if (isFavoritesMode && totalPages > 1) { %>
        <div class="pagination">
            <% if (currentPage > 1) { %>
                <a class="tab-button" href="<%=request.getContextPath()%>/manage/<%=q.getId()%>?mode=favorites&page=<%=currentPage - 1%>#answer-list-top">←</a>
            <% } %>

            <% for (int i = 1; i <= totalPages; i++) { %>
                <a class="tab-button <%= i == currentPage ? "active" : "" %>"
                   href="<%=request.getContextPath()%>/manage/<%=q.getId()%>?mode=favorites&page=<%=i%>#answer-list-top">
                   <%= i %>
                </a>
            <% } %>

            <% if (currentPage < totalPages) { %>
                <a class="tab-button" href="<%=request.getContextPath()%>/manage/<%=q.getId()%>?mode=favorites&page=<%=currentPage + 1%>#answer-list-top">→</a>
            <% } %>
        </div>
        <% } %>
    </section>

</div>

<script>
const contextPath = "<%=request.getContextPath()%>";
const questionId = "<%=q.getId()%>";
const currentPage = "<%=currentPage%>";
const pageMode = "<%=mode%>";

function setBusyOnCard(card, busy) {
    if (!card) return;
    if (busy) {
        card.classList.add("updating");
    } else {
        card.classList.remove("updating");
    }
}

function setBusy(button, busy) {
    if (!button) return;
    button.disabled = busy;
}

function setButtonState(button, active, activeText, inactiveText) {
    if (!button) return;

    if (active) {
        button.classList.add("active");
    } else {
        button.classList.remove("active");
    }

    const label = button.querySelector(".button-label");
    if (label) {
        label.textContent = active ? activeText : inactiveText;
    } else {
        button.textContent = active ? activeText : inactiveText;
    }
}

function shouldShowChip(chipName) {
    if (pageMode === "favorites") {
        return chipName === "special" || chipName === "final";
    }

    if (pageMode === "special" || pageMode === "final") {
        return false;
    }

    return true;
}

function setChipVisible(card, chipName, visible) {
    if (!card) return;

    const chip = card.querySelector('[data-chip="' + chipName + '"]');
    if (!chip) return;

    if (!shouldShowChip(chipName)) {
        chip.classList.add("is-hidden");
        return;
    }

    if (visible) {
        chip.classList.remove("is-hidden");
    } else {
        chip.classList.add("is-hidden");
    }
}

function findCardByAnswerId(answerId) {
    return document.querySelector('.answer-card[data-answer-id="' + answerId + '"]');
}

async function postJson(url, data) {
    const params = new URLSearchParams(data);

    const res = await fetch(url, {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded; charset=UTF-8",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: params.toString()
    });

    let json = {};
    try {
        json = await res.json();
    } catch (e) {
        json = {};
    }

    if (!res.ok || !json.ok) {
        throw new Error("更新失敗");
    }

    return json;
}

async function toggleFavorite(button) {
    const answerId = button.dataset.answerId;
    const card = findCardByAnswerId(answerId);

    setBusy(button, true);
    setBusyOnCard(card, true);

    try {
        const json = await postJson(contextPath + "/favorite", {
            questionId: questionId,
            answerId: answerId
        });

        setButtonState(
            button,
            json.favorite,
            "★ お気に入り中",
            "☆ お気に入りにする"
        );
        setChipVisible(card, "favorite", json.favorite);
    } catch (e) {
        alert("お気に入りの更新に失敗しました");
    } finally {
        setBusy(button, false);
        setBusyOnCard(card, false);
    }
}

function reflectSpecialState(card, specialType) {
    if (!card) return;

    const specialButton = card.querySelector('[data-role="special-toggle"]');
    const chihuahuaButton = card.querySelector('[data-role="chihuahua-toggle"]');

    const isSpecial = specialType === "special";
    const isChihuahua = specialType === "chihuahua";

    if (specialButton) {
        setButtonState(
            specialButton,
            isSpecial,
            "✓ 特別枠に設定中",
            "特別枠にする"
        );
    }

    if (chihuahuaButton) {
        setButtonState(
            chihuahuaButton,
            isChihuahua,
            "🏆 チワワ賞に設定中",
            "チワワ賞にする"
        );
    }

    setChipVisible(card, "special", isSpecial);
    setChipVisible(card, "chihuahua", isChihuahua);
}

async function toggleSpecial(button) {
    const answerId = button.dataset.answerId;
    const card = findCardByAnswerId(answerId);
    const chihuahuaButton = card ? card.querySelector('[data-role="chihuahua-toggle"]') : null;

    setBusy(button, true);
    if (chihuahuaButton) setBusy(chihuahuaButton, true);
    setBusyOnCard(card, true);

    try {
        const json = await postJson(contextPath + "/mark-answer", {
            questionId: questionId,
            answerId: answerId,
            action: "special"
        });

        reflectSpecialState(card, json.specialType);
    } catch (e) {
        alert("特別枠の更新に失敗しました");
    } finally {
        setBusy(button, false);
        if (chihuahuaButton) setBusy(chihuahuaButton, false);
        setBusyOnCard(card, false);
    }
}

async function toggleChihuahua(button) {
    const answerId = button.dataset.answerId;
    const card = findCardByAnswerId(answerId);
    const specialButton = card ? card.querySelector('[data-role="special-toggle"]') : null;

    setBusy(button, true);
    if (specialButton) setBusy(specialButton, true);
    setBusyOnCard(card, true);

    try {
        const json = await postJson(contextPath + "/mark-answer", {
            questionId: questionId,
            answerId: answerId,
            action: "chihuahua"
        });

        reflectSpecialState(card, json.specialType);
    } catch (e) {
        alert("チワワ賞の更新に失敗しました");
    } finally {
        setBusy(button, false);
        if (specialButton) setBusy(specialButton, false);
        setBusyOnCard(card, false);
    }
}

function reflectFinalSelection(selectedAnswerId) {
    const buttons = document.querySelectorAll('[data-role="final-toggle"]');

    buttons.forEach(function(button) {
        const card = button.closest(".answer-card");
        const isSelected = button.dataset.answerId === String(selectedAnswerId);

        setButtonState(
            button,
            isSelected,
            "✓ 決勝進出中",
            "決勝にする"
        );
        setChipVisible(card, "final", isSelected);
    });
}

async function toggleFinal(button) {
    const answerId = button.dataset.answerId;
    const allFinalButtons = document.querySelectorAll('[data-role="final-toggle"]');

    allFinalButtons.forEach(function(btn) {
        setBusy(btn, true);
        const card = btn.closest(".answer-card");
        setBusyOnCard(card, true);
    });

    try {
        const json = await postJson(contextPath + "/mark-answer", {
            questionId: questionId,
            answerId: answerId,
            action: "final",
            page: currentPage
        });

        reflectFinalSelection(json.selectedAnswerId);
    } catch (e) {
        alert("決勝の更新に失敗しました");
    } finally {
        allFinalButtons.forEach(function(btn) {
            setBusy(btn, false);
            const card = btn.closest(".answer-card");
            setBusyOnCard(card, false);
        });
    }
}
</script>

</body>
</html>