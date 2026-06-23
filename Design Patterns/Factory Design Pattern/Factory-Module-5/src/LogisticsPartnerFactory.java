public class LogisticsPartnerFactory {

    private LogisticsPartnerFactory() {
        // Utility class
    }

    public static DeliveryPartner getPartner(DeliveryRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Delivery request cannot be null");
        }
        DeliveryType deliveryType = request.getDeliveryType();
        String destinationCountry = request.getDestinationCountry().toUpperCase();
        boolean isCashOnDelivery = request.isCashOnDelivery();
        double weightKg = request.getWeightKg();
        double distanceKm = request.getDistanceKm();
        return switch (deliveryType) {
            case STANDARD -> {
                checkStandardDeliveryBusinessConditions(destinationCountry);
                yield new StandardDeliveryPartner();
            }
            case EXPRESS -> {
                checkExpressDeliveryBusinessConditions(destinationCountry, weightKg);
                yield new ExpressDeliveryPartner();
            }
            case SAME_DAY -> {
                checkSameDayDeliveryBusinessConditions(destinationCountry, distanceKm, weightKg);
                yield new SameDayDeliveryPartner();
            }
            case INTERNATIONAL -> {
                checkInternationalDeliveryBusinessConditions(destinationCountry, isCashOnDelivery);
                yield new InternationalDeliveryPartner();
            }
            default -> throw new IllegalArgumentException("Delivery type not recognized");
        };
    }

    private static void checkStandardDeliveryBusinessConditions(String destinationCountry) {
        if (!destinationCountry.equals(countries.INDIA.toString())) {
            throw new IllegalArgumentException("Standard delivery business conditions are only supported in " + countries.INDIA);
        }
    }

    private static void checkExpressDeliveryBusinessConditions(String destinationCountry, double packageWeight) {
        if (!destinationCountry.equals(countries.INDIA.toString())) {
            throw new IllegalArgumentException("Express delivery conditions are only supported in " + countries.INDIA);
        }
        if (packageWeight > 30) {
            throw new IllegalArgumentException("Express delivery conditions are not supported due to package weight being more than 30");
        }
    }

    private static void checkSameDayDeliveryBusinessConditions(String destinationCountry, double deliveryDistance, double deliveryWeight) {
        if (!destinationCountry.equals(countries.INDIA.toString())) {
            throw new IllegalArgumentException("Same Day delivery conditions are only supported in " + countries.INDIA);
        }
        if (deliveryDistance > 50) {
            throw new IllegalArgumentException(DeliveryType.SAME_DAY + " delivery conditions are not supported due to delivery distance being more than 50KM");
        }
        if (deliveryWeight > 10) {
            throw new IllegalArgumentException(DeliveryType.SAME_DAY + " delivery conditions are not supported due to delivery weight being more than 10KG");
        }
    }

    private static void checkInternationalDeliveryBusinessConditions(String destinationCountry, boolean isCod) {
        if (destinationCountry.equals(countries.INDIA.toString())) {
            throw new IllegalArgumentException("International delivery conditions are not supported in " + countries.INDIA);
        }
        if (isCod) {
            throw new IllegalArgumentException("Cash on Delivery conditions are not supported in " + DeliveryType.INTERNATIONAL + " delivery conditions");
        }
    }

    private enum countries{
        INDIA
    }
}
