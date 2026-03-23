package pen;

public class CapStrategy implements OpenCloseStrategy {

    @Override
    public void open() {
        System.out.println("Pulling off the cap.");
    }

    @Override
    public void close() {
        System.out.println("Replacing the cap.");
    }
}
