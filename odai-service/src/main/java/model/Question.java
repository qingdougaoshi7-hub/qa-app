package model;

public class Question {

    private String id;
    private String title;
    private int limit;
    private int answerCount;
    private int ownerUserId;

    public Question(String id, String title, int limit) {
        this.id = id;
        this.title = title;
        this.limit = limit;
        this.answerCount = 0;
        this.ownerUserId = 0;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public int getLimit() {
        return limit;
    }

    public int getAnswerCount() {
        return answerCount;
    }

    public void setAnswerCount(int answerCount) {
        this.answerCount = answerCount;
    }

    public int getOwnerUserId() {
        return ownerUserId;
    }

    public void setOwnerUserId(int ownerUserId) {
        this.ownerUserId = ownerUserId;
    }

    public boolean isUnlimited() {
        return limit == -1;
    }

    public boolean canAcceptAnswer() {
        return isUnlimited() || answerCount < limit;
    }
}