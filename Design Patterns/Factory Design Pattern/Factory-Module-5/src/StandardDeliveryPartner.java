import java.util.UUID;

public class StandardDeliveryPartner implements DeliveryPartner{

    private static final int ESTIMATED_DELIVERY_DAYS = 5;

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
                "Standard delivery partner assigned successfully",
                ESTIMATED_DELIVERY_DAYS,
                estimatedCost,
                "STD-" + request.getOrderId() + ":" + UUID.randomUUID());
    }

    @Override
    public String getPartnerName() {
        return this.getClass().getSimpleName();
    }
}
