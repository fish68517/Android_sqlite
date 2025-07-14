package com.example.booktracker.entity;

import java.io.Serializable;

public class Order implements Serializable {

    /**
     * 订单id
     */
    private Integer id;

    /**
     * 用户id
     */
    private Integer userId;

    /**
     * 图书id
     */
    private Integer bookId;

    /**
     * 图书名称
     */
    private String bookName;

    /**
     * 图书封面
     */
    private String bookUrl;

    /**
     * 购买数量
     */
    private Integer quantity;

    /**
     * 创建时间
     */
    private String createTime;

    /**
     * 订单状态：0-待发货，1-已发货，2-已收货
     */
    private Integer status;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getBookId() {
        return bookId;
    }

    public void setBookId(Integer bookId) {
        this.bookId = bookId;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookUrl() {
        return bookUrl;
    }

    public void setBookUrl(String bookUrl) {
        this.bookUrl = bookUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
} 