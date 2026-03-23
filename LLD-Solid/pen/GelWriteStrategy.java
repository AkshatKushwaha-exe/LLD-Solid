package pen;

public class GelWriteStrategy implements WriteStrategy {

    @Override
    public void write() {
        System.out.println("Laying down bold, dark gel ink strokes.");
    }
}
