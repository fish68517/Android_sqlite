package com.example.xiaoshuo.models;

import java.util.Date;

/**
 * 评论模型类
 */
public class Comment {
    private long id;
    private long postId;
    private String content;
    private String authorName;
    private String authorAvatar;
    private Date publishTime;
    private int likeCount;
    private boolean isLiked;
    private Long replyToCommentId; // 如果是回复其他评论，这里是被回复评论的ID
    private String replyToUserName; // 被回复用户的名称

    public Comment() {
        this.publishTime = new Date();
    }

    public Comment(long id, long postId, String content, String authorName, String authorAvatar) {
        this.id = id;
        this.postId = postId;
        this.content = content;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.publishTime = new Date();
        this.likeCount = 0;
        this.isLiked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getPostId() {
        return postId;
    }

    public void setPostId(long postId) {
        this.postId = postId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
    }

    public Date getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(Date publishTime) {
        this.publishTime = publishTime;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public Long getReplyToCommentId() {
        return replyToCommentId;
    }

    public void setReplyToCommentId(Long replyToCommentId) {
        this.replyToCommentId = replyToCommentId;
    }

    public String getReplyToUserName() {
        return replyToUserName;
    }

    public void setReplyToUserName(String replyToUserName) {
        this.replyToUserName = replyToUserName;
    }

    public boolean isReply() {
        return replyToCommentId != null;
    }
} 