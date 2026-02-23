import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("=== Hostel Fee Calculator ===");
        BookingRequest req = new BookingRequest(LegacyRoomTypes.DOUBLE, List.of(AddOn.LAUNDRY, AddOn.MESS));
        HostelFeeCalculator calc = new HostelFeeCalculator(
                new FakeBookingRepo(),
                new DefaultRoomPricing(),
                new DefaultAddOnPricing()
        );
        calc.process(req);
    }
}
