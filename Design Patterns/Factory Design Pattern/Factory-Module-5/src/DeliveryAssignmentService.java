public class DeliveryAssignmentService {

    public DeliveryAssignmentResult assignDeliveryPartner(DeliveryRequest request) {
        DeliveryPartner partner = LogisticsPartnerFactory.getPartner(request);
        return partner.assignDelivery(request);
    }
}