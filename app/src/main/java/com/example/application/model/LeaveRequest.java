package com.example.application.model;

public class LeaveRequest {
    private int id;
    private int userId;
    private String reason;
    private String leaveTime;
    private String status; // "待审批", "已批准", "已拒绝"

    public static final String STATUS_PENDING = "待审批";
    public static final String STATUS_APPROVED = "已批准";
    public static final String STATUS_REJECTED = "已拒绝";


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}