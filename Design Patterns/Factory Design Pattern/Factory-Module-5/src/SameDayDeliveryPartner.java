import java.util.UUID;

public class SameDayDeliveryPartner implements DeliveryPartner{

    private static final int estimatedDeliveryDays = 1;

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
                "Same-day delivery partner assigned successfully",
                estimatedDeliveryDays,
                estimatedCost,
                "SDD-" + request.getOrderId() + ":" + UUID.randomUUID());
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
