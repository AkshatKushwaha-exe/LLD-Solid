package com.example.map;

public class MapMarker {

    private final double latitude;
    private final double longitude;
    private final String label;
    private final MarkerStyle style;

    public MapMarker(double latitude, double longitude, String label, MarkerStyle style) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.label = label;
        this.style = style;
    }

    public double getLatitude() { return latitude; }
    public double getLongitude() { return longitude; }
    public String getLabel() { return label; }
    public MarkerStyle getStyle() { return style; }
}
