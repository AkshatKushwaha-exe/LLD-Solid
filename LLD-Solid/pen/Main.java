package pen;

public class Main {
    public static void main(String[] args) {
        Pen gelPen = PenFactory.createPen(PenType.GEL, "Blue", MechanismType.CLICK);
        Pen fountainPen = PenFactory.createPen(PenType.INK, "Black", MechanismType.CAP);

        try {
            gelPen.write();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }

        try {
            gelPen.start();
            gelPen.write();
            gelPen.close();

            System.out.println("-------------------");

            fountainPen.start();
            fountainPen.write();
            fountainPen.refill("Red");
            fountainPen.write();
            fountainPen.close();
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
