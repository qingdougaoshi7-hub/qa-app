<%@ page pageEncoding="UTF-8" contentType="text/html; charset=UTF-8" %>
<%@ page import="model.Question" %>

<%
    Question q = (Question) request.getAttribute("question");
%>

<!DOCTYPE html>
<html lang="ja">
<head>
<meta charset="UTF-8">
<title><%= q.getTitle() %> | 回答ページ</title>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/style.css">

<style>
.answer-page-header{
    width:100%;
    background:#111;
    box-shadow:0 2px 10px rgba(0,0,0,0.28);
}

.answer-page-header-inner{
    max-width:1100px;
    margin:0 auto;
    padding:14px 20px;
    display:flex;
    align-items:center;
    justify-content:flex-start;
}

.answer-page-logo{
    color:#fff;
    text-decoration:none;
    font-size:22px;
    font-weight:bold;
    white-space:nowrap;
    pointer-events:none;
    cursor:default;
}

.answer-page-wrap{
    max-width: 920px;
    margin: 0 auto;
}

.answer-links-card{
    background:#fffaf0;
    border-radius:24px;
    box-shadow:0 10px 24px rgba(0,0,0,0.10);
    padding:20px 22px;
    margin-bottom:20px;
}

.answer-page-socials{
    display:flex;
    flex-wrap:wrap;
    gap:10px;
    width:100%;
    margin-bottom:14px;
}

.answer-page-social-link{
    display:inline-flex;
    align-items:center;
    justify-content:center;
    min-height:36px;
    padding:7px 14px;
    border-radius:999px;
    background:#1f1f1f;
    color:#fff;
    text-decoration:none;
    font-size:13px;
    font-weight:bold;
    line-height:1.2;
    transition:background 0.18s ease, transform 0.18s ease, opacity 0.18s ease;
    word-break:break-all;
}

.answer-page-social-link:hover{
    background:#2c2c2c;
    transform:translateY(-1px);
}

.answer-detail-box{
    border-top:1px solid #eadfca;
    padding-top:12px;
}

.answer-detail-toggle{
    width:100%;
    border:none;
    background:transparent;
    display:flex;
    align-items:center;
    justify-content:space-between;
    gap:12px;
    padding:4px 0;
    cursor:pointer;
    color:#1f1f1f;
    font-size:15px;
    font-weight:bold;
    text-align:left;
}

.answer-detail-title{
    display:flex;
    align-items:center;
    gap:8px;
}

.answer-detail-arrow{
    display:inline-block;
    transition:transform 0.2s ease;
    font-size:14px;
    color:#6b6252;
}

.answer-detail-toggle.open .answer-detail-arrow{
    transform:rotate(180deg);
}

.answer-detail-content{
    display:none;
    margin-top:12px;
    background:#fff;
    border:1px solid #eadfca;
    border-radius:18px;
    padding:14px 16px;
    color:#3c3427;
    font-size:14px;
    line-height:1.9;
    white-space:pre-line;
}

.answer-detail-content.open{
    display:block;
}

.answer-hero-card{
    background: linear-gradient(180deg, #fff8eb 0%, #fff4db 100%);
    border-radius: 28px;
    box-shadow: 0 12px 28px rgba(0,0,0,0.12);
    padding: 34px;
    margin-bottom: 24px;
    position: relative;
    overflow: hidden;
}

.answer-hero-card::before{
    content: "";
    position: absolute;
    top: -80px;
    right: -60px;
    width: 220px;
    height: 220px;
    background: radial-gradient(circle, rgba(255,255,255,0.5) 0%, rgba(255,255,255,0) 70%);
    pointer-events: none;
}

.answer-kicker{
    display: inline-flex;
    align-items: center;
    justify-content: center;
    min-height: 34px;
    padding: 6px 14px;
    border-radius: 999px;
    background: #1f1f1f;
    color: #fff;
    font-size: 13px;
    font-weight: bold;
    margin-bottom: 14px;
}

.answer-page-title{
    margin: 0 0 12px;
    font-size: 40px;
    line-height: 1.25;
    word-break: break-word;
}

.answer-page-desc{
    margin: 0;
    color: #5f5646;
    font-size: 16px;
    line-height: 1.9;
}

.answer-form-card{
    background: #fffaf0;
    border-radius: 28px;
    box-shadow: 0 10px 24px rgba(0,0,0,0.10);
    padding: 30px;
}

.answer-form-modern{
    display: flex;
    flex-direction: column;
    gap: 18px;
}

.answer-label-row{
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 14px;
    flex-wrap: wrap;
}

.answer-label-main{
    font-size: 16px;
    font-weight: bold;
    color: #1f1f1f;
}

.answer-helper{
    font-size: 13px;
    color: #6b6252;
}

.answer-input-wrap{
    position: relative;
}

.answer-textarea-modern{
    width: 100%;
    min-height: 320px;
    padding: 22px 22px 56px;
    border: 2px solid #eadfca;
    border-radius: 24px;
    background: #ffffff;
    font-size: 19px;
    line-height: 1.9;
    color: #222;
    resize: vertical;
    transition: border-color 0.2s ease, box-shadow 0.2s ease, transform 0.2s ease;
    box-shadow: inset 0 1px 0 rgba(255,255,255,0.8);
}

.answer-textarea-modern::placeholder{
    color: #8e8679;
}

.answer-textarea-modern:focus{
    outline: none;
    border-color: #b98a20;
    box-shadow: 0 0 0 5px rgba(185,138,32,0.14);
    transform: translateY(-1px);
}

.answer-bottom-row{
    position: absolute;
    left: 18px;
    right: 18px;
    bottom: 14px;
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 12px;
    pointer-events: none;
}

.answer-bottom-tip{
    font-size: 12px;
    color: #8a7b62;
    background: rgba(255,248,235,0.92);
    padding: 4px 10px;
    border-radius: 999px;
    max-width: 70%;
    white-space: nowrap;
    overflow: hidden;
    text-overflow: ellipsis;
}

.answer-count{
    font-size: 12px;
    font-weight: bold;
    color: #6b5d43;
    background: rgba(255,248,235,0.96);
    padding: 4px 10px;
    border-radius: 999px;
}

.answer-actions-row{
    display: flex;
    justify-content: space-between;
    align-items: center;
    gap: 16px;
    flex-wrap: wrap;
}

.answer-sub-note{
    color: #6b6252;
    font-size: 13px;
    line-height: 1.7;
}

.answer-submit-button{
    min-width: 170px;
    min-height: 56px;
    border: none;
    border-radius: 18px;
    background: #1f1f1f;
    color: #fff;
    font-size: 17px;
    font-weight: bold;
    cursor: pointer;
    box-shadow: 0 8px 18px rgba(0,0,0,0.18);
    transition: transform 0.18s ease, background 0.18s ease, opacity 0.18s ease, box-shadow 0.18s ease;
}

.answer-submit-button:hover{
    background: #2a2a2a;
    transform: translateY(-2px);
    box-shadow: 0 10px 22px rgba(0,0,0,0.22);
}

.answer-submit-button:disabled{
    opacity: 0.6;
    cursor: wait;
    transform: none;
}

.answer-quick-guide{
    display: grid;
    grid-template-columns: repeat(3, minmax(0, 1fr));
    gap: 12px;
    margin-top: 8px;
}

.answer-guide-item{
    background: #fff;
    border: 1px solid #eadfca;
    border-radius: 18px;
    padding: 14px;
    box-shadow: 0 3px 10px rgba(0,0,0,0.04);
}

.answer-guide-title{
    font-size: 13px;
    font-weight: bold;
    margin-bottom: 6px;
    color: #1f1f1f;
}

.answer-guide-text{
    font-size: 12px;
    color: #6b6252;
    line-height: 1.7;
}

@media (max-width: 768px){
    .answer-page-header-inner{
        padding:14px 16px;
    }

    .answer-page-logo{
        font-size:20px;
    }

    .answer-page-socials{
        gap:8px;
    }

    .answer-page-social-link{
        min-height:32px;
        padding:6px 12px;
        font-size:12px;
    }

    .answer-links-card,
    .answer-hero-card,
    .answer-form-card{
        padding: 20px;
        border-radius: 20px;
    }

    .answer-page-title{
        font-size: 28px;
    }

    .answer-page-desc{
        font-size: 14px;
    }

    .answer-textarea-modern{
        min-height: 240px;
        font-size: 17px;
        padding: 18px 16px 52px;
        border-radius: 18px;
    }

    .answer-actions-row{
        flex-direction: column;
        align-items: stretch;
    }

    .answer-submit-button{
        width: 100%;
        min-height: 52px;
        border-radius: 16px;
        font-size: 16px;
    }

    .answer-quick-guide{
        grid-template-columns: 1fr;
    }
}

@media (max-width: 480px){
    .answer-page-title{
        font-size: 24px;
    }

    .answer-kicker{
        min-height: 30px;
        padding: 5px 12px;
        font-size: 12px;
    }

    .answer-label-main{
        font-size: 15px;
    }

    .answer-helper,
    .answer-sub-note,
    .answer-detail-content{
        font-size: 12px;
    }

    .answer-textarea-modern{
        min-height: 220px;
        font-size: 16px;
        line-height: 1.85;
    }

    .answer-bottom-tip{
        max-width: 60%;
        font-size: 11px;
    }

    .answer-count{
        font-size: 11px;
    }

    .answer-page-social-link{
        width:100%;
        justify-content:flex-start;
    }
}
</style>
</head>
<body>

<div class="answer-page-header">
    <div class="answer-page-header-inner">
        <div class="answer-page-logo">合法的留年チワワ</div>
    </div>
</div>

<div class="page-container">
    <div class="answer-page-wrap">

        <div class="answer-links-card">
            <div class="answer-page-socials">
                <a class="answer-page-social-link" href="http://www.youtube.com/@%E5%90%88%E6%B3%95%E7%9A%84%E7%95%99%E5%B9%B4%E3%83%81%E3%83%AF%E3%83%AF" target="_blank" rel="noopener noreferrer">YouTube</a>
                <a class="answer-page-social-link" href="https://instagram.com/kyugaku_chiwawa" target="_blank" rel="noopener noreferrer">Instagram</a>
                <a class="answer-page-social-link" href="https://twitter.com/KyugakuChiwawa?s=20" target="_blank" rel="noopener noreferrer">X / Twitter</a>
                <a class="answer-page-social-link" href="https://twitch.tv/chiwachiwa000" target="_blank" rel="noopener noreferrer">Twitch</a>
            </div>

            <div class="answer-detail-box">
                <button type="button" class="answer-detail-toggle" id="answerDetailToggle">
                    <span class="answer-detail-title">合法的留年チワワ詳細</span>
                    <span class="answer-detail-arrow">▼</span>
                </button>

                <div class="answer-detail-content" id="answerDetailContent">「孤独歴短いくせに孤独を語ってる、孤独系兼ツッコミ系YouTuber」
「少ない視聴者を反省会で減らした実績あり！」
「過去の配信で250人同接がいたことを誇っています！※250人を200人というと癇癪を起こすのでで気を付けよう！」
「面白くないコメントをすると親ごと殺されるので注意！自信がない人は親の金でスパチャしよう！　※お金が投げられたときはみんなで『ナイスパ！！』とコメントしよう！」
「視聴者はみんな個性豊か！癇癪もちで人の鼻を折ったり、仕事サボって曲作ったり、片親だったりと社会のゴミが集まってます！」</div>
            </div>
        </div>

        <div class="answer-hero-card">
            <div class="answer-kicker">回答ページ</div>
            <h1 class="answer-page-title"><%= q.getTitle() %></h1>
            
        </div>

        <div class="answer-form-card">
            <form action="${pageContext.request.contextPath}/answer" method="post" class="answer-form-modern" id="answerForm">
                <input type="hidden" name="id" value="<%= q.getId() %>">

                <div class="answer-label-row">
                    <div class="answer-label-main">回答を書く</div>
                    <div class="answer-helper">短くても長くても大丈夫</div>
                </div>

                <div class="answer-input-wrap">
                    <textarea
                        id="answer"
                        name="answer"
                        class="answer-textarea-modern"
                        placeholder="ここに回答を書いてください"
                        required
                    ></textarea>

                    <div class="answer-bottom-row">
                        
                        <div class="answer-count"><span id="answerCount">0</span>文字</div>
                    </div>
                </div>

                <div class="answer-actions-row">
                    <div class="answer-sub-note">
                        入力後にそのまま送信できます。<br>
                        送信ボタンを押したら少し待ってください。
                    </div>
                    <button type="submit" class="answer-submit-button" id="submitButton">送信する</button>
                </div>

                
            </form>
        </div>

    </div>
</div>

<script>
(function () {
    const textarea = document.getElementById("answer");
    const count = document.getElementById("answerCount");
    const form = document.getElementById("answerForm");
    const submitButton = document.getElementById("submitButton");
    const detailToggle = document.getElementById("answerDetailToggle");
    const detailContent = document.getElementById("answerDetailContent");

    function updateCount() {
        count.textContent = textarea.value.length;
    }

    textarea.addEventListener("input", updateCount);
    updateCount();

    form.addEventListener("submit", function () {
        submitButton.disabled = true;
        submitButton.textContent = "送信中...";
    });

    detailToggle.addEventListener("click", function () {
        const isOpen = detailContent.classList.contains("open");

        if (isOpen) {
            detailContent.classList.remove("open");
            detailToggle.classList.remove("open");
        } else {
            detailContent.classList.add("open");
            detailToggle.classList.add("open");
        }
    });
})();
</script>

</body>
</html>