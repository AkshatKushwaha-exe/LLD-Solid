package pen;

public class BallpointWriteStrategy implements WriteStrategy {

    @Override
    public void write() {
        System.out.println("Writing smoothly using a ballpoint tip.");
    }
}
