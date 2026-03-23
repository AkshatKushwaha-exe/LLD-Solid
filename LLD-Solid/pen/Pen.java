package pen;

public class Pen {

    private String inkColor;
    private boolean opened;
    private final WriteStrategy writeStrategy;
    private final RefillStrategy refillStrategy;
    private final OpenCloseStrategy openCloseStrategy;

    public Pen(String inkColor, WriteStrategy writeStrategy, RefillStrategy refillStrategy,
               OpenCloseStrategy openCloseStrategy) {
        this.inkColor = inkColor;
        this.writeStrategy = writeStrategy;
        this.refillStrategy = refillStrategy;
        this.openCloseStrategy = openCloseStrategy;
        this.opened = false;
    }

    public void start() {
        openCloseStrategy.open();
        this.opened = true;
    }

    public void close() {
        openCloseStrategy.close();
        this.opened = false;
    }

    public void write() throws Exception {
        if (!opened) {
            throw new Exception("Pen is not open! Call start() before writing.");
        }
        System.out.print("[" + inkColor.toUpperCase() + "] ");
        writeStrategy.write();
    }

    public void refill(String newColor) {
        refillStrategy.refill();
        this.inkColor = newColor;
        System.out.println("Ink color changed to " + newColor + ".");
    }
}
