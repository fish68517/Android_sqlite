package com.archive.app.model;

public class Booking {
    private long id;
    private long userId;
    private long attractionId;
    private String bookingDate;
    private String status;
    private String attractionName; // Transient field for display purposes

    // Constructors
    public Booking() {
    }

    public Booking(long id, long userId, long attractionId, String bookingDate, String status) {
        this.id = id;
        this.userId = userId;
        this.attractionId = attractionId;
        this.bookingDate = bookingDate;
        this.status = status;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public String getBookingDate() {
        return bookingDate;
    }

    public void setBookingDate(String bookingDate) {
        this.bookingDate = bookingDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAttractionName() {
        return attractionName;
    }

    public void setAttractionName(String attractionName) {
        this.attractionName = attractionName;
    }
} 