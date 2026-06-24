import java.util.Map;
import java.util.HashMap;
import java.time.LocalDateTime;

public class NotificationRequest {

    private final String notificationId;
    private final NotificationProvider provider;
    private final NotificationType type;
    private final RecipientDetails recipientDetails;
    private final String templateCode;
    private final Map<String,String> templateVariables;
    private final NotificationPriority priority;
    private final LocalDateTime createdAt;

    public NotificationRequest(
            String notificationId,
            NotificationProvider provider,
            NotificationType type,
            RecipientDetails recipientDetails,
            String templateCode,
            NotificationPriority priority,
            Map<String, String> templateVariables
    ) {
        if (notificationId == null || notificationId.isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank");
        }

        if (provider == null) {
            throw new IllegalArgumentException("Notification provider cannot be null");
        }

        if (type == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }

        if (recipientDetails == null) {
            throw new IllegalArgumentException("Recipient details cannot be null");
        }

        if (templateCode == null || templateCode.isBlank()) {
            throw new IllegalArgumentException("Template code cannot be null or blank");
        }

        if (priority == null) {
            throw new IllegalArgumentException("Notification priority cannot be null");
        }

        if (templateVariables == null) {
            throw new IllegalArgumentException("Template variables cannot be null");
        }

        this.notificationId = notificationId;
        this.provider = provider;
        this.type = type;
        this.recipientDetails = recipientDetails;
        this.templateCode = templateCode;
        this.priority = priority;
        this.templateVariables = Map.copyOf(templateVariables);
        this.createdAt = LocalDateTime.now();
    }

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationProvider getProvider() {
        return provider;
    }

    public NotificationType getType() {
        return type;
    }

    public RecipientDetails getRecipientDetails() {
        return recipientDetails;
    }

    public String getTemplateCode() {
        return templateCode;
    }

    public Map<String,String> getTemplateVariables() {
        return Map.copyOf(templateVariables);
    }

    public NotificationPriority getPriority() {
        return priority;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}
