public interface DeliveryPartner {
    DeliveryAssignmentResult assignDelivery(DeliveryRequest request);
    String getPartnerName();
}