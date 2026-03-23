# Pen - Strategy + Factory Pattern

## Overview
Models different types of pens (ballpoint, gel, fountain) using the Strategy pattern for writing, refilling, and open/close mechanisms. A Factory handles object creation.

## Design
- **WriteStrategy**: pluggable writing behavior per pen type
- **OpenCloseStrategy**: cap vs click mechanism
- **RefillStrategy**: bottle-based vs tube-based refill
- **PenFactory**: maps `PenType` + `MechanismType` to concrete strategy combinations

## How to Run
```bash
javac *.java
java pen.Main
```
