package com.example.orderfood.model;

public class SalesStats {
    private String dishName;
    private int salesCount;
    private float revenue;
    private String orderDate;
    private int dishId;

    public SalesStats(String dishName, int salesCount, float revenue) {
        this.dishName = dishName;
        this.salesCount = salesCount;
        this.revenue = revenue;
    }

    public String getDishName() {
        return dishName;
    }

    public void setDishName(String dishName) {
        this.dishName = dishName;
    }

    public int getSalesCount() {
        return salesCount;
    }

    public void setSalesCount(int salesCount) {
        this.salesCount = salesCount;
    }

    public float getRevenue() {
        return revenue;
    }

    public void setRevenue(float revenue) {
        this.revenue = revenue;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public int getDishId() {
        return dishId;
    }

    public void setDishId(int dishId) {
        this.dishId = dishId;
    }

    @Override
    public String toString() {
        return "SalesStats{" +
                "dishName='" + dishName + '\'' +
                ", salesCount=" + salesCount +
                ", revenue=" + revenue +
                ", orderDate='" + orderDate + '\'' +
                ", dishId=" + dishId +
                '}';
    }
}