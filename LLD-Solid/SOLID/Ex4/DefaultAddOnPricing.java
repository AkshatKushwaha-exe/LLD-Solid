public class DefaultAddOnPricing implements AddOnPricing {
    @Override
    public double getPrice(AddOn addOn) {
        return switch (addOn) {
            case AddOn.MESS -> 1000.0;
            case AddOn.LAUNDRY -> 500.0;
            case AddOn.GYM -> 300.0;
        };
    }
}
