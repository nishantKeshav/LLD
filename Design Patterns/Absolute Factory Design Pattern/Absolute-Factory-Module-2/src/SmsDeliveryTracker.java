public class SmsDeliveryTracker implements DeliveryTracker {

    @Override
    public DeliveryTrackingResult track(DeliveryTrackingRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Delivery tracking request cannot be null.");
        }

        if (request.getProviderMessageId() == null || request.getProviderMessageId().isBlank()) {
            throw new IllegalArgumentException("Provider message ID cannot be null or blank.");
        }

        return new DeliveryTrackingResult(
                request.getNotificationId(),
                NotificationProvider.SMS,
                request.getProviderMessageId(),
                DeliveryStatus.SENT,
                NotificationProvider.SMS + " notification delivered successfully"
        );
    }
}
