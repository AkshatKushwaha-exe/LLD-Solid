package com.example.map;

import java.util.List;

public class MapRenderer {

    private static final int DISPLAY_LIMIT = 8;

    public void render(List<MapMarker> markers) {
        System.out.println("Rendering " + markers.size() + " markers...");
        int displayed = 0;

        for (MapMarker marker : markers) {
            if (displayed < DISPLAY_LIMIT) {
                System.out.println(formatMarker(marker));
                displayed++;
            }
        }

        int remaining = markers.size() - displayed;
        if (remaining > 0) {
            System.out.println("... (" + remaining + " more not shown)");
        }
    }

    private String formatMarker(MapMarker m) {
        return String.format("%s @ (%.4f, %.4f) style=%s",
                m.getLabel(), m.getLatitude(), m.getLongitude(), m.getStyle());
    }
}
