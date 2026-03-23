package com.example.map;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FlyweightMarkersTest {

    private static int passed = 0;
    private static int failed = 0;

    public static void main(String[] args) {

        System.out.println("=== Flyweight Markers Test Suite ===\n");

        // ---------------------------------------------------------------
        // Test 1: MarkerStyle toString format (filled=true -> F, filled=false -> O)
        // ---------------------------------------------------------------
        try {
            MarkerStyle filledStyle = new MarkerStyle("PIN", "RED", 10, true);
            MarkerStyle openStyle = new MarkerStyle("CIRCLE", "BLUE", 14, false);

            String filledStr = filledStyle.toString();
            String openStr = openStyle.toString();

            boolean pass = filledStr.equals("PIN|RED|10|F") && openStr.equals("CIRCLE|BLUE|14|O");
            report("Test  1: MarkerStyle toString format (F for filled, O for open)", pass,
                    pass ? null : "Expected 'PIN|RED|10|F' and 'CIRCLE|BLUE|14|O', got '" + filledStr + "' and '" + openStr + "'");
        } catch (Exception e) {
            report("Test  1: MarkerStyle toString format (F for filled, O for open)", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 2: MarkerStyle is immutable (all getters return constructor values)
        // ---------------------------------------------------------------
        try {
            MarkerStyle style = new MarkerStyle("SQUARE", "GREEN", 16, true);

            boolean pass = style.getShape().equals("SQUARE")
                    && style.getColor().equals("GREEN")
                    && style.getSize() == 16
                    && style.isFilled() == true;
            report("Test  2: MarkerStyle immutability (getters match constructor)", pass,
                    pass ? null : "Getters did not return constructor values: shape=" + style.getShape()
                            + " color=" + style.getColor() + " size=" + style.getSize() + " filled=" + style.isFilled());
        } catch (Exception e) {
            report("Test  2: MarkerStyle immutability (getters match constructor)", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 3: Factory returns same instance for same params (== reference equality)
        // ---------------------------------------------------------------
        try {
            MarkerStyleFactory factory = new MarkerStyleFactory();
            MarkerStyle s1 = factory.getStyle("PIN", "RED", 10, true);
            MarkerStyle s2 = factory.getStyle("PIN", "RED", 10, true);

            boolean pass = (s1 == s2);
            report("Test  3: Factory same params -> same reference (==)", pass,
                    pass ? null : "Expected same reference but got two distinct objects");
        } catch (Exception e) {
            report("Test  3: Factory same params -> same reference (==)", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 4: Factory returns different instances for different params
        // ---------------------------------------------------------------
        try {
            MarkerStyleFactory factory = new MarkerStyleFactory();
            MarkerStyle s1 = factory.getStyle("PIN", "RED", 10, true);
            MarkerStyle s2 = factory.getStyle("CIRCLE", "BLUE", 14, false);

            boolean pass = (s1 != s2);
            report("Test  4: Factory different params -> different instances", pass,
                    pass ? null : "Expected different references but got the same object");
        } catch (Exception e) {
            report("Test  4: Factory different params -> different instances", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 5: Factory cache size matches number of unique style combos requested
        // ---------------------------------------------------------------
        try {
            MarkerStyleFactory factory = new MarkerStyleFactory();
            factory.getStyle("PIN", "RED", 10, true);
            factory.getStyle("PIN", "RED", 10, true);   // duplicate
            factory.getStyle("CIRCLE", "BLUE", 14, false);
            factory.getStyle("SQUARE", "GREEN", 12, true);
            factory.getStyle("CIRCLE", "BLUE", 14, false); // duplicate

            int cacheSize = factory.getCacheSize();
            boolean pass = (cacheSize == 3);
            report("Test  5: Factory cache size equals unique combo count (3)", pass,
                    pass ? null : "Expected cache size 3, got " + cacheSize);
        } catch (Exception e) {
            report("Test  5: Factory cache size equals unique combo count (3)", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 6: MapMarker stores latitude, longitude, label, style correctly
        // ---------------------------------------------------------------
        try {
            MarkerStyle style = new MarkerStyle("PIN", "RED", 10, true);
            MapMarker marker = new MapMarker(12.9716, 77.5946, "Office", style);

            boolean pass = Math.abs(marker.getLatitude() - 12.9716) < 1e-9
                    && Math.abs(marker.getLongitude() - 77.5946) < 1e-9
                    && marker.getLabel().equals("Office")
                    && marker.getStyle() == style;
            report("Test  6: MapMarker stores lat/lng/label/style correctly", pass,
                    pass ? null : "MapMarker getters did not match constructor args");
        } catch (Exception e) {
            report("Test  6: MapMarker stores lat/lng/label/style correctly", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 7: MapDataSource loadMarkers returns requested count
        // ---------------------------------------------------------------
        try {
            MapDataSource ds = new MapDataSource();
            List<MapMarker> markers = ds.loadMarkers(100);

            boolean pass = (markers.size() == 100);
            report("Test  7: MapDataSource loadMarkers returns correct count (100)", pass,
                    pass ? null : "Expected 100 markers, got " + markers.size());
        } catch (Exception e) {
            report("Test  7: MapDataSource loadMarkers returns correct count (100)", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 8: MapDataSource all markers have non-null styles
        // ---------------------------------------------------------------
        try {
            MapDataSource ds = new MapDataSource();
            List<MapMarker> markers = ds.loadMarkers(200);

            boolean allNonNull = true;
            for (MapMarker m : markers) {
                if (m.getStyle() == null) {
                    allNonNull = false;
                    break;
                }
            }
            report("Test  8: All loaded markers have non-null styles", allNonNull,
                    allNonNull ? null : "Found marker with null style");
        } catch (Exception e) {
            report("Test  8: All loaded markers have non-null styles", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 9: Flyweight sharing - 10000 markers, unique style identities <= 96
        // ---------------------------------------------------------------
        try {
            MapDataSource ds = new MapDataSource();
            List<MapMarker> markers = ds.loadMarkers(10000);

            Set<Integer> identitySet = new HashSet<>();
            for (MapMarker m : markers) {
                identitySet.add(System.identityHashCode(m.getStyle()));
            }

            boolean pass = (identitySet.size() <= 96);
            report("Test  9: Flyweight sharing (10000 markers, unique styles <= 96)", pass,
                    pass ? null : "Expected at most 96 unique style identities, got " + identitySet.size());
        } catch (Exception e) {
            report("Test  9: Flyweight sharing (10000 markers, unique styles <= 96)", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 10: Factory with same inputs called 1000 times always returns same reference
        // ---------------------------------------------------------------
        try {
            MarkerStyleFactory factory = new MarkerStyleFactory();
            MarkerStyle first = factory.getStyle("PIN", "ORANGE", 12, false);

            boolean allSame = true;
            for (int i = 0; i < 1000; i++) {
                MarkerStyle current = factory.getStyle("PIN", "ORANGE", 12, false);
                if (current != first) {
                    allSame = false;
                    break;
                }
            }
            report("Test 10: Same factory input x1000 -> always same reference", allSame,
                    allSame ? null : "Got a different reference on one of the 1000 calls");
        } catch (Exception e) {
            report("Test 10: Same factory input x1000 -> always same reference", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 11: All 3 shapes x 4 colors x 4 sizes x 2 filled = 96 unique styles
        // ---------------------------------------------------------------
        try {
            MarkerStyleFactory factory = new MarkerStyleFactory();
            String[] shapes = {"PIN", "CIRCLE", "SQUARE"};
            String[] colors = {"RED", "BLUE", "GREEN", "ORANGE"};
            int[] sizes = {10, 12, 14, 16};
            boolean[] filledOpts = {true, false};

            Set<Integer> identitySet = new HashSet<>();
            for (String shape : shapes) {
                for (String color : colors) {
                    for (int size : sizes) {
                        for (boolean f : filledOpts) {
                            MarkerStyle s = factory.getStyle(shape, color, size, f);
                            identitySet.add(System.identityHashCode(s));
                        }
                    }
                }
            }

            boolean pass = (factory.getCacheSize() == 96) && (identitySet.size() == 96);
            report("Test 11: All combos (3x4x4x2=96) produce 96 unique cached styles", pass,
                    pass ? null : "Cache size=" + factory.getCacheSize() + ", identity count=" + identitySet.size());
        } catch (Exception e) {
            report("Test 11: All combos (3x4x4x2=96) produce 96 unique cached styles", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 12: MapMarker getStyle returns shared factory-created instance
        // ---------------------------------------------------------------
        try {
            MarkerStyleFactory factory = new MarkerStyleFactory();
            MarkerStyle shared = factory.getStyle("SQUARE", "ORANGE", 16, true);
            MapMarker m1 = new MapMarker(13.0, 77.6, "A", shared);
            MapMarker m2 = new MapMarker(13.1, 77.7, "B", shared);

            boolean pass = (m1.getStyle() == m2.getStyle()) && (m1.getStyle() == shared);
            report("Test 12: MapMarker getStyle returns shared factory instance", pass,
                    pass ? null : "Markers do not share the same style reference");
        } catch (Exception e) {
            report("Test 12: MapMarker getStyle returns shared factory instance", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 13: Zero markers load returns empty list
        // ---------------------------------------------------------------
        try {
            MapDataSource ds = new MapDataSource();
            List<MapMarker> markers = ds.loadMarkers(0);

            boolean pass = (markers != null && markers.isEmpty());
            report("Test 13: loadMarkers(0) returns empty list", pass,
                    pass ? null : "Expected empty list, got " + (markers == null ? "null" : "size " + markers.size()));
        } catch (Exception e) {
            report("Test 13: loadMarkers(0) returns empty list", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Test 14: Large load (50000 markers) still bounded style count (<= 96)
        // ---------------------------------------------------------------
        try {
            MapDataSource ds = new MapDataSource();
            List<MapMarker> markers = ds.loadMarkers(50000);

            Set<Integer> identitySet = new HashSet<>();
            for (MapMarker m : markers) {
                identitySet.add(System.identityHashCode(m.getStyle()));
            }

            boolean pass = (markers.size() == 50000) && (identitySet.size() <= 96);
            report("Test 14: Large load (50000 markers) still bounded styles <= 96", pass,
                    pass ? null : "marker count=" + markers.size() + ", unique styles=" + identitySet.size());
        } catch (Exception e) {
            report("Test 14: Large load (50000 markers) still bounded styles <= 96", false, e.toString());
        }

        // ---------------------------------------------------------------
        // Summary
        // ---------------------------------------------------------------
        System.out.println("\n=== Test Summary ===");
        System.out.println("Passed: " + passed);
        System.out.println("Failed: " + failed);
        System.out.println("Total:  " + (passed + failed));
        System.out.println("Result: " + (failed == 0 ? "ALL TESTS PASSED" : failed + " TEST(S) FAILED"));
    }

    private static void report(String name, boolean pass, String detail) {
        if (pass) {
            System.out.println("[PASS] " + name);
            passed++;
        } else {
            System.out.println("[FAIL] " + name);
            if (detail != null) {
                System.out.println("       -> " + detail);
            }
            failed++;
        }
    }
}
