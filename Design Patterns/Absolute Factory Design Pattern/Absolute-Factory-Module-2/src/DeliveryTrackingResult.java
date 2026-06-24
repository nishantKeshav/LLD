public class DeliveryTrackingResult {

    private final String notificationId;
    private final NotificationProvider provider;
    private final String providerMessageId;
    private final DeliveryStatus deliveryStatus;
    private final String message;

    public  DeliveryTrackingResult(String notificationId, NotificationProvider provider, String providerMessageId,
                                   DeliveryStatus deliveryStatus, String message) {
        this.notificationId = notificationId;
        this.provider = provider;
        this.providerMessageId = providerMessageId;
        this.deliveryStatus = deliveryStatus;
        this.message = message;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationProvider getProvider() {
        return provider;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getMessage() {
        return message;
    }
}
