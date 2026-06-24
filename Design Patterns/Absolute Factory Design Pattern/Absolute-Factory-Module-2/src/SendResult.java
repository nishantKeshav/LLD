public class SendResult {

    private final String notificationId;
    private final NotificationProvider provider;
    private final boolean success;
    private final String providerMessageId;
    private final String message;
    private final String failureMessage;

    public SendResult(String notificationId, NotificationProvider provider, boolean success, String providerMessageId,
                      String message, String failureMessage) {
        this.notificationId = notificationId;
        this.provider = provider;
        this.success = success;
        this.providerMessageId = providerMessageId;
        this.message = message;
        this.failureMessage = failureMessage;
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

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public String getMessage() {
        return message;
    }

    public String getFailureMessage() {
        return failureMessage;
    }

}
