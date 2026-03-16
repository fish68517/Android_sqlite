package com.hakimi.model;

public class ChatMessage {
    public static final int TYPE_USER = 1; // 用户发送
    public static final int TYPE_AI = 2;   // AI助手发送

    private String content;
    private int type;

    public ChatMessage(String content, int type) {
        this.content = content;
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}