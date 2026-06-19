import java.time.LocalDateTime;

public class DeliveryRequest {

    private final String requestId;
    private final String orderId;
    private final String customerId;
    private final String sourceCity;
    private final String destinationCity;
    private final String destinationCountry;
    private final DeliveryType deliveryType;
    private final double distanceKm;
    private final double weightKg;
    private final double packageValue;
    private final boolean fragile;
    private final boolean cashOnDelivery;
    private final LocalDateTime createdAt;

    public DeliveryRequest(String requestId, String orderId, String customerId, String sourceCity,
                           String destinationCity, String destinationCountry, DeliveryType deliveryType,
                           double distanceKm, double weightKg, double packageValue, boolean fragile, boolean cashOnDelivery) {
        if (requestId == null || requestId.isBlank()) {
            throw new IllegalArgumentException("requestId cannot be null or blank");
        }
        if (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId cannot be null or blank");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId cannot be null or blank");
        }
        if (sourceCity == null || sourceCity.isBlank()) {
            throw new IllegalArgumentException("sourceCity cannot be null or blank");
        }
        if (destinationCity == null || destinationCity.isBlank()) {
            throw new IllegalArgumentException("destinationCity cannot be null or blank");
        }
        if (destinationCountry == null || destinationCountry.isBlank()) {
            throw new IllegalArgumentException("destinationCountry cannot be null or blank");
        }
        if (deliveryType == null) {
            throw new IllegalArgumentException("deliveryType cannot be null");
        }
        if (distanceKm <= 0) {
            throw new IllegalArgumentException("distanceKm must be grater than zero");
        }
        if (weightKg <= 0) {
            throw new IllegalArgumentException("weightKg must be grater than zero");
        }
        if (packageValue <= 0) {
            throw new IllegalArgumentException("packageValue must be grater than zero");
        }
        this.requestId = requestId;
        this.orderId = orderId;
        this.customerId = customerId;
        this.sourceCity = sourceCity;
        this.destinationCity = destinationCity;
        this.destinationCountry = destinationCountry;
        this.deliveryType = deliveryType;
        this.distanceKm = distanceKm;
        this.weightKg = weightKg;
        this.packageValue = packageValue;
        this.fragile = fragile;
        this.cashOnDelivery = cashOnDelivery;
        this.createdAt = LocalDateTime.now();
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getSourceCity() {
        return sourceCity;
    }

    public String getDestinationCity() {
        return destinationCity;
    }

    public String getDestinationCountry() {
        return destinationCountry;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public double getDistanceKm() {
        return distanceKm;
    }

    public double getWeightKg() {
        return weightKg;
    }

    public double getPackageValue() {
        return packageValue;
    }

    public boolean isFragile() {
        return fragile;
    }

    public boolean isCashOnDelivery() {
        return cashOnDelivery;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
