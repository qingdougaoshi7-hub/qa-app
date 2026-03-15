package model;

public class Answer {

    private int answerId;
    private String text;
    private boolean favorite;
    private boolean read;

    private String specialType;   // "", "chihuahua", "special"
    private boolean finalFlag;    // 決勝入り
    private int finalRank;        // 1〜5, 未設定は0

    public Answer(int answerId, String text) {
        this.answerId = answerId;
        this.text = text;
        this.favorite = false;
        this.read = false;
        this.specialType = "";
        this.finalFlag = false;
        this.finalRank = 0;
    }

    public int getAnswerId() {
        return answerId;
    }

    public String getText() {
        return text;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void toggleFavorite() {
        this.favorite = !this.favorite;
    }

    public boolean isRead() {
        return read;
    }

    public void markRead() {
        this.read = true;
    }

    public String getSpecialType() {
        return specialType;
    }

    public void setSpecialType(String specialType) {
        this.specialType = specialType;
    }

    public boolean isFinalFlag() {
        return finalFlag;
    }

    public void setFinalFlag(boolean finalFlag) {
        this.finalFlag = finalFlag;
    }

    public int getFinalRank() {
        return finalRank;
    }

    public void setFinalRank(int finalRank) {
        this.finalRank = finalRank;
    }

    public boolean isChihuahuaAward() {
        return "chihuahua".equals(specialType);
    }

    public boolean isSpecialFrame() {
        return "special".equals(specialType);
    }
}