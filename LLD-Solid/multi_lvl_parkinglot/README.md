# Multi-Level Parking Lot

Object-oriented design for a multi-level parking lot system following SOLID principles.

## Architecture
- **Strategy Pattern** for both slot assignment (`SlotAssignmentStrategy`) and pricing (`PricingStrategy`), making the system extensible
- **3D Euclidean Distance** to find the nearest available slot from an entry gate, factoring in floor differences
- **Entity Classes**: `Vehicle`, `Slot`, `Gate`, `Ticket`, `ParkingLot`

## Design Trade-offs
The current slot search is O(N) linear scan. For production-scale systems, a min-heap per slot type would give O(1) retrieval. Thread safety is omitted to keep the core logic focused.

## How to Run
```bash
javac *.java
java multi_lvl_parkinglot.Main
```
