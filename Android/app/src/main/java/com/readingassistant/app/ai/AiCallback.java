package com.readingassistant.app.ai;

public interface AiCallback {
    void onSuccess(String summary);

    void onFailure(String message);
}
