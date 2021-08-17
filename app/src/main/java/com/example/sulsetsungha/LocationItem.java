package com.example.sulsetsungha;

public class LocationItem {
    int resourceId;
    String state;
    String distance;

    public LocationItem(int resourceId, String state, String distance) {
        this.resourceId = resourceId;
        this.state = state;
        this.distance = distance;

    }

    public int getResourceId() {
        return resourceId;
    }

    public String getState() {
        return state;
    }

    public String getDistance() {
        return String.valueOf(distance);
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
