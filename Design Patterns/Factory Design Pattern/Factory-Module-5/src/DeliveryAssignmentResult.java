public class DeliveryAssignmentResult {

    private final String requestId;
    private final String orderId;
    private final String customerId;
    private final DeliveryType deliveryType;
    private final String partnerName;
    private final boolean assigned;
    private final String message;
    private final int estimatedDeliveryDays;
    private final double estimatedCost;
    private final String trackingId;

    public DeliveryAssignmentResult(String requestId, String orderId, String customerId, DeliveryType deliveryType,
                                    String partnerName, boolean assigned, String message, int estimatedDeliveryDays,
                                    double estimatedCost, String trackingId) {
        this.orderId = orderId;
        this.requestId = requestId;
        this.customerId = customerId;
        this.deliveryType = deliveryType;
        this.partnerName = partnerName;
        this.assigned = assigned;
        this.message = message;
        this.estimatedDeliveryDays = estimatedDeliveryDays;
        this.estimatedCost = estimatedCost;
        this.trackingId = trackingId;
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

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public String getPartnerName() {
        return partnerName;
    }

    public boolean isAssigned() {
        return assigned;
    }

    public String getMessage() {
        return message;
    }

    public int getEstimatedDeliveryDays() {
        return estimatedDeliveryDays;
    }

    public double getEstimatedCost() {
        return estimatedCost;
    }

    public String getTrackingId() {
        return trackingId;
    }
}
