package com.example.orderfood.model;

import java.util.Date;

/**
 * 订单表 Orders
 */
public class Orders {
    private int orderId;
    private String orderTime;

    private String remark;
    private int studentId;
    private int merchantId;
    private String dishList;
    private double totalPrice;
    private String orderStatus;
    private int dishId;

    private String diningOption;

    private int orderQuantity;

    public int getOrderQuantity() {
        return orderQuantity;
    }

    public void setOrderQuantity(int orderQuantity) {
        this.orderQuantity = orderQuantity;
    }

    public String getDiningOption() {
        return diningOption;
    }

    public void setDiningOption(String diningOption) {
        this.diningOption = diningOption;
    }

    public int getDishId() {
        return dishId;
    }



    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public int getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(int merchantId) {
        this.merchantId = merchantId;
    }

    public String getDishList() {
        return dishList;
    }

    public void setDishList(String dishList) {
        this.dishList = dishList;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public void setDishId(int dishListId) {
        this.dishId = dishListId;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderId=" + orderId +
                ", orderTime='" + orderTime + '\'' +
                ", studentId=" + studentId +
                ", merchantId=" + merchantId +
                ", dishList='" + dishList + '\'' +
                ", totalPrice=" + totalPrice +
                ", orderStatus='" + orderStatus + '\'' +
                ", dishId=" + dishId +
                ", diningOption='" + diningOption + '\'' +
                ", orderQuantity=" + orderQuantity +
                '}';
    }
}
