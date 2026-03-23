package pen;

public class BottleRefillStrategy implements RefillStrategy {

    @Override
    public void refill() {
        System.out.println("Dipping the converter into an ink bottle to refill.");
    }
}
