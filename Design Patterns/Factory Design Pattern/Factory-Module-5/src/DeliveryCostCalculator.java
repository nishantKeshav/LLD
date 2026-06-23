public class DeliveryCostCalculator {

    private static final double PACKAGE_VALUE_THRESHOLD = 10000;

    private DeliveryCostCalculator() {
        // Utility class
    }

    public static double calculateCost(DeliveryRequest request) {
        double baseCost = 0;
        double perKm = 0;
        double perKg = 0;
        switch (request.getDeliveryType()) {
            case STANDARD -> {
                baseCost = 50;
                perKm = 2;
                perKg = 10;
            }
            case EXPRESS -> {
                baseCost = 100;
                perKm = 4;
                perKg = 15;
            }
            case SAME_DAY -> {
                baseCost = 150;
                perKm = 6;
                perKg = 20;
            }
            case INTERNATIONAL -> {
                baseCost = 500;
                perKm = 8;
                perKg = 50;
            }
            default -> throw new IllegalArgumentException("Unsupported delivery type.");
        }
        double cost = baseCost + (request.getDistanceKm() * perKm) + (request.getWeightKg() * perKg);
        if (request.isFragile()) {
            cost += 100;
        }
        if (request.isCashOnDelivery()) {
            cost += 40;
        }
        if (request.getPackageValue() > packageValueThreshold) {
            cost += request.getPackageValue() * 0.01;
        }
        return cost;
    }
}