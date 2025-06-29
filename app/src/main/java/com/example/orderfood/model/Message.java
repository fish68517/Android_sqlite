package com.example.orderfood.model;

public class Message {
    private long id;
    private int senderId;
    private int receiverId;
    private String content;
    private String timestamp;
    private boolean isRetracted;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isRetracted() {
        return isRetracted;
    }

    public void setRetracted(boolean retracted) {
        isRetracted = retracted;
    }
} 