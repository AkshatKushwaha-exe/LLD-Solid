# Elevator System

An object-oriented implementation of a multi-elevator system in Java.

## Architecture

- **Strategy Pattern**: `ElevatorDispatchStrategy` interface with `NearestElevatorStrategy` for selecting which elevator serves a hall call.
- **Entities**: `Elevator`, `Floor`, `Request`, `Display`, `InternalButton`, `ExternalButton`
- **Orchestrator**: `ElevatorController` manages all elevators and floors, validates requests, delegates dispatch.

## Key Features

- Per-elevator weight limits (configurable). Overweight triggers alarm + door open + movement block.
- Emergency button stops that specific elevator, clears its queue, rings alarm.
- Floors can be put under maintenance — requests to that floor are rejected.
- Elevator states: MOVING_UP, MOVING_DOWN, IDLE, MAINTENANCE.

## How to Run

```bash
cd elevator-system
javac *.java
java Main
```
