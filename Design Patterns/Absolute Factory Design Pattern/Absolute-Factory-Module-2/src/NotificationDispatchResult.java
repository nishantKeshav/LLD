public class NotificationDispatchResult {

    private final String notificationId;
    private final NotificationProvider provider;
    private final boolean success;
    private final DeliveryStatus deliveryStatus;
    private final String providerMessageId;
    private final String message;
    private final RetryDecision retryDecision;
    private final String failureReason;

    public NotificationDispatchResult(String notificationId, NotificationProvider provider, boolean success, DeliveryStatus deliveryStatus,
                                      String providerMessageId, String message, RetryDecision retryDecision, String failureReason) {
        this.notificationId = notificationId;
        this.provider = provider;
        this.deliveryStatus = deliveryStatus;
        this.providerMessageId = providerMessageId;
        this.message = message;
        this.retryDecision = retryDecision;
        this.failureReason = failureReason;
        this.success = success;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationProvider getProvider() {
        return provider;
    }

    public boolean isSuccess() {
        return success;
    }

    public DeliveryStatus getDeliveryStatus() {
        return deliveryStatus;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getMessage() {
        return message;
    }

    public RetryDecision getRetryDecision() {
        return retryDecision;
    }

    public String getFailureReason() {
        return failureReason;
    }

}
