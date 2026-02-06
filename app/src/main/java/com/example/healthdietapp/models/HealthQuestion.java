package com.example.healthdietapp.models;

/**
 * HealthQuestion - Model for daily health Q&A content
 */
public class HealthQuestion {
    private String questionId;
    private String question;
    private String answer;
    private String category;
    private long createdAt;

    public HealthQuestion() {
    }

    public HealthQuestion(String questionId, String question, String answer, String category, long createdAt) {
        this.questionId = questionId;
        this.question = question;
        this.answer = answer;
        this.category = category;
        this.createdAt = createdAt;
    }

    // Getters and Setters
    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
