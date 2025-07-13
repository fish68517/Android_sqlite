package com.example.xiaoshuo.models;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 社区帖子模型类
 */
public class Post {
    private long id;
    private String title;
    private String content;
    private String authorName;
    private String authorAvatar;
    private Date publishTime;
    private int likeCount;
    private int commentCount;
    private boolean isLiked;
    private List<String> imageUrls;
    private List<Comment> comments;
    private String bookTitle; // 关联的书籍标题，如果有的话
    private int viewCount;
    private String postType; // 帖子类型：讨论、书评、求书等

    public Post() {
        this.imageUrls = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.publishTime = new Date();
    }

    public Post(long id, String title, String content, String authorName, String authorAvatar) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.authorName = authorName;
        this.authorAvatar = authorAvatar;
        this.publishTime = new Date();
        this.imageUrls = new ArrayList<>();
        this.comments = new ArrayList<>();
        this.likeCount = 0;
        this.commentCount = 0;
        this.viewCount = 0;
        this.isLiked = false;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public boolean isLiked() {
        return isLiked;
    }

    public void setLiked(boolean liked) {
        isLiked = liked;
    }

    public List<String> getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = imageUrls;
    }

    public void addImageUrl(String imageUrl) {
        this.imageUrls.add(imageUrl);
    }

    public List<Comment> getComments() {
        return comments;
    }

    public void setComments(List<Comment> comments) {
        this.comments = comments;
        this.commentCount = comments.size();
    }

    public void addComment(Comment comment) {
        this.comments.add(comment);
        this.commentCount = this.comments.size();
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }
} 