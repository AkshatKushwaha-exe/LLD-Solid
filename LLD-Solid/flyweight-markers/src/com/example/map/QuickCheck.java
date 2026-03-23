package com.example.map;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class QuickCheck {

    public static void main(String[] args) {
        int totalMarkers = 20_000;

        MapDataSource dataSource = new MapDataSource();
        List<MapMarker> markers = dataSource.loadMarkers(totalMarkers);

        Set<Integer> uniqueIdentities = new HashSet<>();
        for (MapMarker marker : markers) {
            uniqueIdentities.add(System.identityHashCode(marker.getStyle()));
        }

        System.out.println("Total markers loaded: " + totalMarkers);
        System.out.println("Distinct style object instances: " + uniqueIdentities.size());
        int maxExpected = 3 * 4 * 4 * 2;
        System.out.println("Expected (max unique combinations): <= " + maxExpected);
    }
}
