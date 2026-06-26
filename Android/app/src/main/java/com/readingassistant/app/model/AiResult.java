package com.readingassistant.app.model;

public class AiResult {
    private final boolean success;
    private final String summary;
    private final String errorMessage;

    private AiResult(boolean success, String summary, String errorMessage) {
        this.success = success;
        this.summary = summary == null ? "" : summary;
        this.errorMessage = errorMessage == null ? "" : errorMessage;
    }

    public static AiResult success(String summary) {
        return new AiResult(true, summary, "");
    }

    public static AiResult failure(String errorMessage) {
        return new AiResult(false, "", errorMessage);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getSummary() {
        return summary;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
