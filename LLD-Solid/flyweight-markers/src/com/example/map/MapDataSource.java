package com.example.map;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MapDataSource {

    private static final String[] SHAPES = {"PIN", "CIRCLE", "SQUARE"};
    private static final String[] COLORS = {"RED", "BLUE", "GREEN", "ORANGE"};
    private static final int[] SIZES = {10, 12, 14, 16};

    private final MarkerStyleFactory styleFactory = new MarkerStyleFactory();

    public List<MapMarker> loadMarkers(int count) {
        Random rand = new Random(7);
        List<MapMarker> markers = new ArrayList<>(count);

        for (int i = 0; i < count; i++) {
            double lat = 12.9 + rand.nextDouble() * 0.2;
            double lng = 77.5 + rand.nextDouble() * 0.2;

            String shape = SHAPES[rand.nextInt(SHAPES.length)];
            String color = COLORS[rand.nextInt(COLORS.length)];
            int size = SIZES[rand.nextInt(SIZES.length)];
            boolean filled = rand.nextBoolean();

            MarkerStyle sharedStyle = styleFactory.getStyle(shape, color, size, filled);
            markers.add(new MapMarker(lat, lng, "M-" + i, sharedStyle));
        }

        System.out.println("[StyleFactory] cached unique styles: " + styleFactory.getCacheSize());
        return markers;
    }
}
