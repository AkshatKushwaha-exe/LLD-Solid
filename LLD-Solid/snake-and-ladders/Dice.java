import java.util.Random;

public class Dice {

    private final int faces;
    private final Random rng;

    public Dice(int faces) {
        this.faces = faces;
        this.rng = new Random();
    }

    public int roll() {
        return rng.nextInt(faces) + 1;
    }
}
