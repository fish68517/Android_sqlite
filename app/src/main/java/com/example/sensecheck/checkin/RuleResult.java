package com.example.sensecheck.checkin;

public final class RuleResult {
    private final String result;
    private final String reason;
    private final float distanceMeters;

    public RuleResult(String result, String reason, float distanceMeters) {
        this.result = result;
        this.reason = reason;
        this.distanceMeters = distanceMeters;
    }

    public String getResult() {
        return result;
    }

    public String getReason() {
        return reason;
    }

    public float getDistanceMeters() {
        return distanceMeters;
    }
}

