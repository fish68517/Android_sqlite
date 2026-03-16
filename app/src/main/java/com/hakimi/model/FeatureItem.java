package com.hakimi.model;

public class FeatureItem {
    private String name;
    private int iconResId;

    public FeatureItem(String name, int iconResId) {
        this.name = name;
        this.iconResId = iconResId;
    }

    public String getName() {
        return name;
    }

    public int getIconResId() {
        return iconResId;
    }

}
