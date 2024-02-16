package com.hsm.macs.campusguide;

public class BuildingInfo {
    private String title;
    private String description;

    public BuildingInfo(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}