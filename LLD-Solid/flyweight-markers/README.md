# Flyweight Pattern - Map Markers

## Overview
Refactors a map marker rendering system to use the Flyweight pattern. Instead of each marker storing its own style fields (shape, color, size, filled), styles are shared via a factory-managed cache, reducing memory usage for large datasets (30,000+ markers).

## Design
- `MarkerStyle` (Flyweight): immutable value object holding intrinsic state — no setters, all fields final
- `MarkerStyleFactory`: maintains a `HashMap` cache keyed by `"shape|color|size|F/O"`, returns shared instances via `computeIfAbsent`
- `MapMarker`: holds only extrinsic state (lat, lng, label) and a reference to its shared `MarkerStyle`
- `QuickCheck`: validates that the number of unique `MarkerStyle` object references is bounded by the number of unique style combinations (max 96)

## Build & Run
```bash
cd flyweight-markers/src
javac com/example/map/*.java
java com.example.map.App
```
