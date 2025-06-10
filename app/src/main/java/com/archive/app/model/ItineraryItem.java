package com.archive.app.model;

public class ItineraryItem {
    private long id;
    private long itineraryId;
    private long attractionId;
    private String visitDate;
    private String visitTime;
    private String notes;

    // Constructors
    public ItineraryItem() {
    }

    public ItineraryItem(long id, long itineraryId, long attractionId, String visitDate, String visitTime, String notes) {
        this.id = id;
        this.itineraryId = itineraryId;
        this.attractionId = attractionId;
        this.visitDate = visitDate;
        this.visitTime = visitTime;
        this.notes = notes;
    }

    // Getters and Setters
    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getItineraryId() {
        return itineraryId;
    }

    public void setItineraryId(long itineraryId) {
        this.itineraryId = itineraryId;
    }

    public long getAttractionId() {
        return attractionId;
    }

    public void setAttractionId(long attractionId) {
        this.attractionId = attractionId;
    }

    public String getVisitDate() {
        return visitDate;
    }

    public void setVisitDate(String visitDate) {
        this.visitDate = visitDate;
    }

    public String getVisitTime() {
        return visitTime;
    }

    public void setVisitTime(String visitTime) {
        this.visitTime = visitTime;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
} 