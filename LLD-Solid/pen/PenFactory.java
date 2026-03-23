package pen;

public class PenFactory {

    public static Pen createPen(PenType penType, String color, MechanismType mechanism) {
        WriteStrategy writeBehavior;
        RefillStrategy refillBehavior;

        switch (penType) {
            case BALLPOINT:
                writeBehavior = new BallpointWriteStrategy();
                refillBehavior = new TubeRefillStrategy();
                break;
            case GEL:
                writeBehavior = new GelWriteStrategy();
                refillBehavior = new TubeRefillStrategy();
                break;
            case INK:
                writeBehavior = new InkWriteStrategy();
                refillBehavior = new BottleRefillStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unsupported pen type: " + penType);
        }

        OpenCloseStrategy openCloseBehavior;
        switch (mechanism) {
            case CAP:
                openCloseBehavior = new CapStrategy();
                break;
            case CLICK:
                openCloseBehavior = new ClickStrategy();
                break;
            default:
                throw new IllegalArgumentException("Unsupported mechanism: " + mechanism);
        }

        return new Pen(color, writeBehavior, refillBehavior, openCloseBehavior);
    }
}
