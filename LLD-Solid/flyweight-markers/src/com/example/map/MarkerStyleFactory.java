package com.example.map;

import java.util.HashMap;
import java.util.Map;

public class MarkerStyleFactory {

    private final Map<String, MarkerStyle> styleCache = new HashMap<>();

    public MarkerStyle getStyle(String shape, String color, int size, boolean filled) {
        String cacheKey = shape + "|" + color + "|" + size + "|" + (filled ? "F" : "O");
        return styleCache.computeIfAbsent(cacheKey, k -> new MarkerStyle(shape, color, size, filled));
    }

    public int getCacheSize() {
        return styleCache.size();
    }
}
