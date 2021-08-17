package com.example.sulsetsungha;

public class LocationItem {
    int resourceId;
    String username;
    int distance;

    public LocationItem(int resourceId, String username, int distance) {
        this.resourceId = resourceId;
        this.username = username;
        this.distance = distance;

    }

    public int getResourceId() {
        return resourceId;
    }

    public String getUname() {
        return username;
    }

    public int getDistance() {
        return distance;
    }

    public void setState(String username) {
        this.username = username;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public void setResourceId(int resourceId) {
        this.resourceId = resourceId;
    }
}
