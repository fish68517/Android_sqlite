package com.example.campusguide.model;

public class Place {
    private final int id;
    private final String name;
    private final String category;
    private final String shortDescription;
    private final String description;
    private final String hours;
    private final String distance;
    private final int imageResId;

    public Place(int id, String name, String category, String shortDescription,
                 String description, String hours, String distance, int imageResId) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.shortDescription = shortDescription;
        this.description = description;
        this.hours = hours;
        this.distance = distance;
        this.imageResId = imageResId;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getShortDescription() { return shortDescription; }
    public String getDescription() { return description; }
    public String getHours() { return hours; }
    public String getDistance() { return distance; }
    public int getImageResId() { return imageResId; }
}
