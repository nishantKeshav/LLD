import java.util.UUID;

public class InternationalDeliveryPartner implements DeliveryPartner {

    private static final int estimatedDeliveryDays = 10;

    @Override
    public DeliveryAssignmentResult assignDelivery(DeliveryRequest request) {
        double estimatedCost = DeliveryCostCalculator.calculateCost(request);
        return new DeliveryAssignmentResult(
                request.getRequestId(),
                request.getOrderId(),
                request.getCustomerId(),
                request.getDeliveryType(),
                this.getPartnerName(),
                true,
                "International delivery partner assigned successfully",
                estimatedDeliveryDays,
                estimatedCost,
                "INT-" + request.getOrderId() + ":" + UUID.randomUUID());
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }

}
