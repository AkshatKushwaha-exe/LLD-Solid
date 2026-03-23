package pen;

public class TubeRefillStrategy implements RefillStrategy {

    @Override
    public void refill() {
        System.out.println("Swapping out the ink cartridge tube.");
    }
}
