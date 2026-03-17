(function () {
    var STYLE_ID = "answer-image-style";

    function injectStyle() {
        if (document.getElementById(STYLE_ID)) {
            return;
        }

        var style = document.createElement("style");
        style.id = STYLE_ID;
        style.textContent =
            ".answer-image-button{" +
            "border:none;" +
            "background:#fff;" +
            "color:#222;" +
            "padding:6px 12px;" +
            "border-radius:999px;" +
            "cursor:pointer;" +
            "font-weight:bold;" +
            "font-size:12px;" +
            "line-height:1.1;" +
            "box-shadow:0 2px 8px rgba(0,0,0,0.10);" +
            "transition:transform 0.15s ease, box-shadow 0.15s ease, opacity 0.15s ease;" +
            "}" +
            ".answer-image-button:hover{" +
            "transform:translateY(-1px);" +
            "box-shadow:0 4px 12px rgba(0,0,0,0.14);" +
            "}" +
            ".answer-image-button:disabled{" +
            "opacity:0.65;" +
            "cursor:wait;" +
            "}" +
            ".answer-image-action-row{" +
            "display:flex;" +
            "justify-content:flex-start;" +
            "margin-top:10px;" +
            "}" +
            ".ai-modal{" +
            "position:fixed;" +
            "top:0;" +
            "right:0;" +
            "bottom:0;" +
            "left:0;" +
            "background:rgba(0,0,0,0.6);" +
            "display:none;" +
            "align-items:center;" +
            "justify-content:center;" +
            "z-index:3000;" +
            "padding:20px;" +
            "}" +
            ".ai-modal.open{" +
            "display:flex;" +
            "}" +
            ".ai-modal-panel{" +
            "width:92vw;" +
            "max-width:520px;" +
            "max-height:90vh;" +
            "overflow:auto;" +
            "background:#fff8eb;" +
            "border-radius:24px;" +
            "padding:18px;" +
            "box-shadow:0 18px 50px rgba(0,0,0,0.35);" +
            "}" +
            ".ai-modal-title{" +
            "margin:0 0 14px;" +
            "text-align:center;" +
            "font-size:18px;" +
            "font-weight:bold;" +
            "}" +
            ".ai-modal-preview-wrap{" +
            "background:#f4efe4;" +
            "border-radius:20px;" +
            "padding:12px;" +
            "margin-bottom:14px;" +
            "}" +
            ".ai-modal-preview{" +
            "width:100%;" +
            "border-radius:18px;" +
            "display:block;" +
            "}" +
            ".ai-modal-actions{" +
            "display:flex;" +
            "gap:10px;" +
            "justify-content:center;" +
            "flex-wrap:wrap;" +
            "}" +
            ".ai-modal-button{" +
            "border:none;" +
            "background:#1f1f1f;" +
            "color:#fff;" +
            "padding:10px 16px;" +
            "border-radius:999px;" +
            "cursor:pointer;" +
            "font-weight:bold;" +
            "font-size:13px;" +
            "}" +
            ".ai-modal-button.sub{" +
            "background:#fff;" +
            "color:#222;" +
            "border:1px solid #d7c8a7;" +
            "}";

        document.head.appendChild(style);
    }

    function ensureModal() {
        var modal = document.getElementById("answerImageModal");
        if (modal) {
            return modal;
        }

        modal = document.createElement("div");
        modal.id = "answerImageModal";
        modal.className = "ai-modal";
        modal.innerHTML =
            '<div class="ai-modal-panel">' +
                '<h3 class="ai-modal-title">回答画像プレビュー</h3>' +
                '<div class="ai-modal-preview-wrap">' +
                    '<img id="answerImagePreview" class="ai-modal-preview" alt="回答画像プレビュー">' +
                '</div>' +
                '<div class="ai-modal-actions">' +
                    '<button type="button" id="answerImageSaveBtn" class="ai-modal-button">保存</button>' +
                    '<button type="button" id="answerImageShareBtn" class="ai-modal-button">共有</button>' +
                    '<button type="button" id="answerImageCloseBtn" class="ai-modal-button sub">閉じる</button>' +
                '</div>' +
            '</div>';

        document.body.appendChild(modal);

        modal.addEventListener("click", function (e) {
            if (e.target === modal) {
                closeModal();
            }
        });

        document.getElementById("answerImageCloseBtn").addEventListener("click", closeModal);

        return modal;
    }

    function closeModal() {
        var modal = document.getElementById("answerImageModal");
        if (modal) {
            modal.className = "ai-modal";
        }
    }

    function openModal(dataUrl, fileName) {
        var modal = ensureModal();
        var preview = document.getElementById("answerImagePreview");
        var saveBtn = document.getElementById("answerImageSaveBtn");
        var shareBtn = document.getElementById("answerImageShareBtn");

        preview.src = dataUrl;

        saveBtn.onclick = function () {
            downloadDataUrl(dataUrl, fileName);
        };

        shareBtn.onclick = function () {
            shareImage(dataUrl, fileName);
        };

        if (!navigator.share || !window.File) {
            shareBtn.style.display = "none";
        } else {
            shareBtn.style.display = "inline-flex";
        }

        modal.className = "ai-modal open";
    }

    function downloadDataUrl(dataUrl, fileName) {
        var a = document.createElement("a");
        a.href = dataUrl;
        a.download = fileName;
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
    }

    function shareImage(dataUrl, fileName) {
        if (!navigator.share || !window.File) {
            return;
        }

        fetch(dataUrl)
            .then(function (response) {
                return response.blob();
            })
            .then(function (blob) {
                var file = new File([blob], fileName, { type: "image/png" });
                return navigator.share({
                    files: [file],
                    title: "回答画像",
                    text: "回答画像を共有"
                });
            })
            .catch(function () {});
    }

    function trimText(text) {
        return String(text || "").replace(/^\s+|\s+$/g, "");
    }

    function getQuestionTitle() {
        var selectors = [
            ".page-container .card h1",
            ".page-container .answer-form-card h1",
            ".page-container .search-card h1",
            ".answer-page-title"
        ];
        var i;
        var el;

        for (i = 0; i < selectors.length; i++) {
            el = document.querySelector(selectors[i]);
            if (el && trimText(el.textContent) !== "") {
                return trimText(el.textContent);
            }
        }

        return "合法的留年チワワ";
    }

    function getAnswerText(card) {
        var selectors = [".answer-text", ".ranking-answer-text"];
        var i;
        var el;
        var pList;
        var txt;

        for (i = 0; i < selectors.length; i++) {
            el = card.querySelector(selectors[i]);
            if (el && trimText(el.textContent) !== "") {
                return trimText(el.textContent);
            }
        }

        pList = card.querySelectorAll("p");
        for (i = 0; i < pList.length; i++) {
            txt = trimText(pList[i].textContent);
            if (txt !== "" && txt !== "まだ順位は決まっていません。") {
                return txt;
            }
        }

        return "";
    }

    function getCardRankText(card) {
        var selectors = [".ranking-title", ".ranking-rank", "[data-rank]"];
        var i;
        var el;

        for (i = 0; i < selectors.length; i++) {
            el = card.querySelector(selectors[i]);
            if (el && trimText(el.textContent) !== "") {
                return trimText(el.textContent);
            }
        }

        return "";
    }

    function isRankText(text) {
        var t = trimText(text);
        return t === "1位" || t === "2位" || t === "3位" || t === "4位" || t === "5位";
    }

    function getBadgeTexts(card) {
        var badges = [];
        var rankText = getCardRankText(card);
        var nodes = card.querySelectorAll(".answer-status-chip:not(.is-hidden)");
        var i;
        var text;

        if (rankText !== "") {
            badges.push(rankText);
        }

        for (i = 0; i < nodes.length; i++) {
            text = trimText(nodes[i].textContent);
            if (text !== "" && badges.indexOf(text) === -1) {
                badges.push(text);
            }
        }

        return badges.slice(0, 4);
    }

    function sanitizeFileName(name) {
        return String(name || "").replace(/[\\/:*?"<>|]/g, "_").substring(0, 40);
    }

    function roundRect(ctx, x, y, w, h, r) {
        var radius = Math.min(r, w / 2, h / 2);
        ctx.beginPath();
        ctx.moveTo(x + radius, y);
        ctx.arcTo(x + w, y, x + w, y + h, radius);
        ctx.arcTo(x + w, y + h, x, y + h, radius);
        ctx.arcTo(x, y + h, x, y, radius);
        ctx.arcTo(x, y, x + w, y, radius);
        ctx.closePath();
    }

    function wrapByWidth(ctx, text, maxWidth) {
        var lines = [];
        var paragraphs = String(text || "").split(/\n/);
        var i;
        var j;
        var paragraph;
        var current;
        var ch;
        var test;

        for (i = 0; i < paragraphs.length; i++) {
            paragraph = paragraphs[i];
            if (!paragraph) {
                lines.push("");
                continue;
            }

            current = "";
            for (j = 0; j < paragraph.length; j++) {
                ch = paragraph.charAt(j);
                test = current + ch;
                if (ctx.measureText(test).width > maxWidth && current !== "") {
                    lines.push(current);
                    current = ch;
                } else {
                    current = test;
                }
            }

            if (current !== "") {
                lines.push(current);
            }
        }

        return lines;
    }

    function badgeColor(text) {
        if (text.indexOf("お気に入り") !== -1) return "#b8860b";
        if (text.indexOf("特別枠") !== -1) return "#8f3fb0";
        if (text.indexOf("チワワ賞") !== -1) return "#d97706";
        if (text.indexOf("決勝") !== -1) return "#0f766e";
        return "#555555";
    }

    function rankTheme(rankText) {
        var rank = trimText(rankText);

        if (rank === "1位") return { main: "#d4a017", sub: "#b88710", light: "#f8df83", dark: "#8b6508" };
        if (rank === "2位") return { main: "#adb5bd", sub: "#8f989f", light: "#eceff2", dark: "#6d757c" };
        if (rank === "3位") return { main: "#b86b3d", sub: "#9a562e", light: "#efc0a0", dark: "#6f391b" };

        return { main: "#666666", sub: "#4d4d4d", light: "#bdbdbd", dark: "#333333" };
    }

    function drawBadge(ctx, text, x, y) {
        var paddingX = 14;
        var height = 34;
        var width;

        ctx.font = "bold 16px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        width = Math.ceil(ctx.measureText(text).width) + paddingX * 2;

        ctx.fillStyle = badgeColor(text);
        roundRect(ctx, x, y, width, height, 17);
        ctx.fill();

        ctx.fillStyle = "#ffffff";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.fillText(text, x + width / 2, y + height / 2 + 1);

        return width;
    }

    function drawCenteredBadges(ctx, badges, centerX, y) {
        var widths = [];
        var totalWidth = 0;
        var i;
        var x;

        if (!badges || badges.length === 0) {
            return y;
        }

        ctx.font = "bold 16px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";

        for (i = 0; i < badges.length; i++) {
            widths.push(Math.ceil(ctx.measureText(badges[i]).width) + 28);
            totalWidth += widths[i];
        }

        totalWidth += (badges.length - 1) * 8;
        x = centerX - totalWidth / 2;

        for (i = 0; i < badges.length; i++) {
            drawBadge(ctx, badges[i], x, y);
            x += widths[i] + 8;
        }

        return y + 44;
    }

    function drawLargeRank(ctx, rankText, centerX, y) {
        var theme;
        var ribbonW;
        var ribbonH;
        var ribbonX;
        var ribbonY;
        var medalR;
        var medalY;
        var grad;

        if (!isRankText(rankText)) {
            return y;
        }

        theme = rankTheme(rankText);

        ribbonW = 180;
        ribbonH = 14;
        ribbonX = centerX - ribbonW / 2;
        ribbonY = y + 18;

        ctx.fillStyle = theme.sub;
        roundRect(ctx, ribbonX, ribbonY, ribbonW, ribbonH, 7);
        ctx.fill();

        medalR = 38;
        medalY = y + 24;

        grad = ctx.createRadialGradient(centerX - 8, medalY - 8, 6, centerX, medalY, medalR);
        grad.addColorStop(0, "#ffffff");
        grad.addColorStop(0.45, theme.light);
        grad.addColorStop(1, theme.main);

        ctx.fillStyle = grad;
        ctx.beginPath();
        ctx.arc(centerX, medalY, medalR, 0, Math.PI * 2);
        ctx.fill();

        ctx.lineWidth = 3;
        ctx.strokeStyle = theme.dark;
        ctx.stroke();

        ctx.fillStyle = "#ffffff";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";
        ctx.font = "bold 30px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        ctx.fillText(rankText, centerX, medalY + 1);

        return y + 70;
    }

    function chooseBodyLayout(ctx, answerText, textWidth) {
        var fontSizes = [28, 26, 24, 22, 20, 18];
        var i;
        var fontSize;
        var lineHeight;
        var lines;

        for (i = 0; i < fontSizes.length; i++) {
            fontSize = fontSizes[i];
            ctx.font = "bold " + fontSize + "px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
            lines = wrapByWidth(ctx, answerText, textWidth);
            lineHeight = Math.floor(fontSize * 1.55);

            if (lines.length <= 28) {
                return {
                    fontSize: fontSize,
                    lineHeight: lineHeight,
                    lines: lines
                };
            }
        }

        fontSize = 17;
        ctx.font = "bold " + fontSize + "px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        lines = wrapByWidth(ctx, answerText, textWidth);

        return {
            fontSize: fontSize,
            lineHeight: Math.floor(fontSize * 1.55),
            lines: lines
        };
    }

    function calcTextCardHeight(lineCount, lineHeight) {
        var paddingTop = 34;
        var paddingBottom = 52;
        var minHeight = 300;
        var textHeight = lineCount * lineHeight;
        return Math.max(minHeight, paddingTop + textHeight + paddingBottom);
    }

    function drawTextCard(ctx, x, y, w, answerText) {
        var innerPaddingX = 28;
        var textWidth = w - innerPaddingX * 2;
        var layout = chooseBodyLayout(ctx, answerText, textWidth);
        var fontSize = layout.fontSize;
        var lineHeight = layout.lineHeight;
        var lines = layout.lines;
        var h = calcTextCardHeight(lines.length, lineHeight);
        var textStartY;
        var i;

        ctx.fillStyle = "#f8f8f8";
        roundRect(ctx, x, y, w, h, 22);
        ctx.fill();

        ctx.strokeStyle = "#e5d8bd";
        ctx.lineWidth = 2;
        roundRect(ctx, x, y, w, h, 22);
        ctx.stroke();

        ctx.font = "bold " + fontSize + "px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        ctx.fillStyle = "#2a2a2a";
        ctx.textAlign = "center";
        ctx.textBaseline = "middle";

        textStartY = y + 34 + lineHeight / 2;

        for (i = 0; i < lines.length; i++) {
            ctx.fillText(lines[i], x + w / 2, textStartY);
            textStartY += lineHeight;
        }

        ctx.fillStyle = "#8a6510";
        ctx.textAlign = "center";
        ctx.textBaseline = "top";
        ctx.font = "bold 16px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        ctx.fillText("合法的留年チワワ", x + w / 2, y + h - 28);

        return h;
    }

    function buildImageData(questionTitle, answerText, badges) {
        var canvas = document.createElement("canvas");
        var ctx = canvas.getContext("2d");
        var outerX = 40;
        var outerY = 44;
        var outerW = 640;
        var innerX = 64;
        var innerY = 66;
        var innerW = 592;
        var centerX;
        var titleLines;
        var y;
        var i;
        var rankBadge;
        var otherBadges = [];
        var textCardX;
        var textCardY;
        var textCardW;
        var layout;
        var textCardH;
        var innerBottomY;
        var outerH;

        canvas.width = 720;
        canvas.height = 1100;

        centerX = innerX + innerW / 2;

        ctx.font = "bold 22px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        titleLines = wrapByWidth(ctx, questionTitle, innerW - 40);

        y = innerY + 24;
        y += Math.min(titleLines.length, 2) * 30;

        rankBadge = "";
        for (i = 0; i < badges.length; i++) {
            if (isRankText(badges[i])) {
                rankBadge = badges[i];
            } else {
                otherBadges.push(badges[i]);
            }
        }

        if (rankBadge !== "") {
            y += 2;
            y += 70;
        }

        if (otherBadges.length > 0) {
            y += 2;
            y += 44;
        }

        y += 8;

        textCardX = innerX + 14;
        textCardY = y;
        textCardW = innerW - 28;

        ctx.font = "bold 28px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";
        layout = chooseBodyLayout(ctx, answerText, textCardW - 56);
        textCardH = calcTextCardHeight(layout.lines.length, layout.lineHeight);

        innerBottomY = textCardY + textCardH + 28;
        outerH = (innerBottomY - outerY) + 24;
        canvas.height = Math.max(760, outerY + outerH + 40);

        ctx = canvas.getContext("2d");

        ctx.fillStyle = "#c89d3a";
        ctx.fillRect(0, 0, canvas.width, canvas.height);

        ctx.fillStyle = "#fff8eb";
        roundRect(ctx, outerX, outerY, outerW, outerH, 28);
        ctx.fill();

        ctx.fillStyle = "#f7f1e3";
        roundRect(ctx, innerX, innerY, innerW, innerBottomY - innerY, 22);
        ctx.fill();

        ctx.fillStyle = "#111111";
        ctx.textAlign = "center";
        ctx.textBaseline = "top";
        ctx.font = "bold 22px 'Yu Gothic','Hiragino Sans','Meiryo',sans-serif";

        titleLines = wrapByWidth(ctx, questionTitle, innerW - 40);
        y = innerY + 24;

        for (i = 0; i < titleLines.length && i < 2; i++) {
            ctx.fillText(titleLines[i], centerX, y);
            y += 30;
        }

        rankBadge = "";
        otherBadges = [];

        for (i = 0; i < badges.length; i++) {
            if (isRankText(badges[i])) {
                rankBadge = badges[i];
            } else {
                otherBadges.push(badges[i]);
            }
        }

        if (rankBadge !== "") {
            y += 2;
            y = drawLargeRank(ctx, rankBadge, centerX, y);
        }

        if (otherBadges.length > 0) {
            y += 2;
            y = drawCenteredBadges(ctx, otherBadges, centerX, y);
        }

        y += 8;

        textCardX = innerX + 14;
        textCardY = y;
        textCardW = innerW - 28;

        drawTextCard(ctx, textCardX, textCardY, textCardW, answerText);

        return canvas.toDataURL("image/png");
    }

    function addImageButton(card) {
        var answerText;
        var button;
        var actions;
        var row;
        var target;

        if (card.getAttribute("data-image-button-ready") === "true") {
            return;
        }

        answerText = getAnswerText(card);
        if (answerText === "") {
            return;
        }

        button = document.createElement("button");
        button.type = "button";
        button.className = "answer-image-button";
        button.appendChild(document.createTextNode("画像化"));

        button.addEventListener("click", function () {
            var questionTitle = getQuestionTitle();
            var badges = getBadgeTexts(card);
            var dataUrl = buildImageData(questionTitle, answerText, badges);
            var fileName = sanitizeFileName(questionTitle + "_" + answerText.substring(0, 12)) + ".png";
            openModal(dataUrl, fileName);
        });

        actions = card.querySelector(".answer-actions");
        if (actions) {
            actions.appendChild(button);
        } else {
            row = document.createElement("div");
            row.className = "answer-image-action-row";
            row.appendChild(button);

            target = card.querySelector(".answer-text, .ranking-answer-text, p, .ranking-title, .ranking-rank");
            if (target) {
                card.insertBefore(row, target);
            } else {
                card.appendChild(row);
            }
        }

        card.setAttribute("data-image-button-ready", "true");
    }

    function init() {
        var cards;
        var i;

        injectStyle();
        ensureModal();

        cards = document.querySelectorAll(".answer-card");
        for (i = 0; i < cards.length; i++) {
            addImageButton(cards[i]);
        }
    }

    if (document.readyState === "loading") {
        document.addEventListener("DOMContentLoaded", init);
    } else {
        init();
    }
}());