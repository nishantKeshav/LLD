public class DeliveryTrackingRequest {

    private final String notificationId;
    private final String providerMessageId;
    private final NotificationProvider provider;

    public DeliveryTrackingRequest(String notificationId, String providerMessageId, NotificationProvider provider) {
        if (notificationId == null || notificationId.isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank");
        }
        if (providerMessageId == null || providerMessageId.isBlank()) {
            throw new IllegalArgumentException("Provider Message ID cannot be null or blank");
        }
        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null");
        }
        this.notificationId = notificationId;
        this.providerMessageId = providerMessageId;
        this.provider = provider;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public NotificationProvider getProvider() {
        return provider;
    }
}
