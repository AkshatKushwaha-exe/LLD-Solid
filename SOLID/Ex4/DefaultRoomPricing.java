public class DefaultRoomPricing implements RoomPricing {
    @Override
    public double getBaseMonthly(int roomType) {
        return switch (roomType) {
            case LegacyRoomTypes.SINGLE -> 14000.0;
            case LegacyRoomTypes.DOUBLE -> 15000.0;
            case LegacyRoomTypes.TRIPLE -> 12000.0;
            default -> 16000.0;
        };
    }
}
