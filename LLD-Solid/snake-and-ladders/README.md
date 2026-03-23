# Snake and Ladders

Classic Snake & Ladder board game implemented with clean OO design.

## Design
- **Strategy Pattern**: `MakeMoveStrategy` interface with `EasyMoveStrategy` (extra turns on 6) and `HardMoveStrategy` (3 consecutive 6s forfeits the turn)
- **Factory Pattern**: `GameFactory` encapsulates game setup — board, dice, player queue, and difficulty selection
- **Round-robin turns** via `Queue<Player>`

## How to Run
```bash
javac *.java
java Main
```
