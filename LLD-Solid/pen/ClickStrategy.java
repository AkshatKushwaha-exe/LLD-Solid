package pen;

public class ClickStrategy implements OpenCloseStrategy {

    @Override
    public void open() {
        System.out.println("Pressing the click button to push the nib out.");
    }

    @Override
    public void close() {
        System.out.println("Pressing the click button to pull the nib in.");
    }
}
