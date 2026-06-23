import java.util.List;
import java.time.LocalDateTime;

public class NotificationRequest {

    private final String notificationId;
    private final String tenantId;
    private final String userId;
    private final String recipientEmail;
    private final String recipientPhone;
    private final String deviceToken;
    private final String title;
    private final String message;
    private final NotificationPriority priority;
    private final NotificationChannelType preferredChannel;
    private final List<NotificationChannelType> fallbackChannels;
    private final LocalDateTime createdAt;

    public NotificationRequest(String notificationId, String tenantId, String userId, String recipientEmail, String recipientPhone, String deviceToken,
                               String title, String message, NotificationPriority notificationPriority, NotificationChannelType notificationChannelType,
                               List<NotificationChannelType> fallbackChannels) {
        this.title = title;
        this.userId = userId;
        this.message = message;
        this.tenantId = tenantId;
        this.deviceToken = deviceToken;
        this.notificationId = notificationId;
        this.recipientEmail = recipientEmail;
        this.recipientPhone = recipientPhone;
        this.priority = notificationPriority;
        this.preferredChannel = notificationChannelType;
        this.fallbackChannels = List.copyOf(fallbackChannels);
        this.createdAt = LocalDateTime.now();
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

    public String getRecipientEmail() {
        return recipientEmail;
    }

    public String getRecipientPhone() {
        return recipientPhone;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public NotificationChannelType getPreferredChannel() {
        return preferredChannel;
    }

    public List<NotificationChannelType> getFallbackChannels() {
        return List.copyOf(fallbackChannels);
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

}
