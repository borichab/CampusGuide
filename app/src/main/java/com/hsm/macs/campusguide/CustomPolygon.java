package com.hsm.macs.campusguide;

import android.graphics.Color;

import com.google.android.gms.maps.model.PolygonOptions;

public class CustomPolygon {
    private PolygonOptions polygonOptions;
    private String title;
    private String description;

    public CustomPolygon(PolygonOptions polygonOptions, String title, String description) {
        this.polygonOptions = polygonOptions;
        this.title = title;
        this.description = description;
    }

    public PolygonOptions getPolygonOptions() {
        return polygonOptions;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}

