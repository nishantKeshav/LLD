import java.util.Objects;

public class NotificationRequestValidator {

    private NotificationRequestValidator() {
        // Utility class
    }

    public static void validate(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null.");
        }

        if (request.getNotificationId() == null || request.getNotificationId().isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank.");
        }

        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        if (request.getUserId() == null || request.getUserId().isBlank()) {
            throw new IllegalArgumentException("User ID cannot be null or blank.");
        }

        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new IllegalArgumentException("Title cannot be null or blank.");
        }

        if (request.getMessage() == null || request.getMessage().isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank.");
        }

        if (request.getPriority() == null) {
            throw new IllegalArgumentException("Priority cannot be null.");
        }

        if (request.getPreferredChannel() == null) {
            throw new IllegalArgumentException("Preferred channel cannot be null.");
        }

        if (request.getFallbackChannels() == null) {
            throw new IllegalArgumentException("Fallback channels cannot be null.");
        }

        if (request.getFallbackChannels().stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("Fallback channels cannot contain null values.");
        }
    }
}
