import java.util.List;

public class NotificationResult {

    private final String notificationId;
    private final String tenantId;
    private final String userId;
    private final NotificationChannelType channelUsed;
    private final boolean sent;
    private final String message;
    private final String providerMessageId;
    private final List<NotificationChannelType> attemptedChannels;
    private final String failureReason;

    public NotificationResult(
            String notificationId,
            String tenantId,
            String userId,
            NotificationChannelType channelUsed,
            boolean sent,
            String message,
            String providerMessageId,
            List<NotificationChannelType> attemptedChannels,
            String failureReason
    ) {
        this.notificationId = notificationId;
        this.tenantId = tenantId;
        this.userId = userId;
        this.channelUsed = channelUsed;
        this.sent = sent;
        this.message = message;
        this.providerMessageId = providerMessageId;
        this.attemptedChannels = List.copyOf(attemptedChannels);
        this.failureReason = failureReason;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public String getUserId() {
        return userId;
    }

    public NotificationChannelType getChannelUsed() {
        return channelUsed;
    }

    public boolean isSent() {
        return sent;
    }

    public String getMessage() {
        return message;
    }

    public String getProviderMessageId() {
        return providerMessageId;
    }

    public List<NotificationChannelType> getAttemptedChannels() {
        return attemptedChannels;
    }

    public String getFailureReason() {
        return failureReason;
    }
}