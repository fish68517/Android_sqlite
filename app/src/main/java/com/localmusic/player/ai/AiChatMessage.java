package com.localmusic.player.ai;

public class AiChatMessage {

    public static final String ROLE_USER = "user";
    public static final String ROLE_ASSISTANT = "assistant";

    private final String role;
    private String content;
    private boolean pending;

    public AiChatMessage(String role, String content) {
        this.role = role;
        this.content = content;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isPending() {
        return pending;
    }

    public void setPending(boolean pending) {
        this.pending = pending;
    }
}
