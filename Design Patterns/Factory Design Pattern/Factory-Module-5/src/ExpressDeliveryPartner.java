import java.util.UUID;

public class ExpressDeliveryPartner implements DeliveryPartner {

    private static final int estimatedDeliveryDays = 2;

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
                "Express delivery partner assigned successfully",
                estimatedDeliveryDays,
                estimatedCost,
                "EXP-" + request.getOrderId() + ":" + UUID.randomUUID());
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
