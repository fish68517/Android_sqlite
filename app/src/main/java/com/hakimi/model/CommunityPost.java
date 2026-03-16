package com.hakimi.model;

import java.io.Serializable;
import java.util.List;

/**
 * 社区动态实体
 * 
 * @author hakimi
 */
public class CommunityPost implements Serializable {
    private Long id;
    private Long userId;
    private String content;
    private String topic;
    private Integer likeCount;
    private Integer commentCount;
    private Integer shareCount;
    private Integer viewCount;
    private Integer status;
    private String createTime;
    private List<String> imageUrls;
    private User user;

    public CommunityPost() {
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public Integer getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(Integer likeCount) {
        this.likeCount = likeCount;
    }

    public Integer getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(Integer commentCount) {
        this.commentCount = commentCount;
    }

    public Integer getShareCount() {
        return shareCount;
    }

    public void setShareCount(Integer shareCount) {
        this.shareCount = shareCount;
    }

    public Integer getViewCount() {
        return viewCount;
    }

    public void setViewCount(Integer viewCount) {
        this.viewCount = viewCount;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public String toString() {
        return "CommunityPost{" +
                "id=" + id +
                ", userId=" + userId +
                ", content='" + content + '\'' +
                ", topic='" + topic + '\'' +
                ", likeCount=" + likeCount +
                ", commentCount=" + commentCount +
                ", shareCount=" + shareCount +
                ", viewCount=" + viewCount +
                ", status=" + status +
                ", createTime='" + createTime + '\'' +
                ", imageUrls=" + imageUrls +
                ", user=" + user +
                '}';
    }
}

