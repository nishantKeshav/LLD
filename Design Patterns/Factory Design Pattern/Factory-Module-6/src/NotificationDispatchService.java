import java.util.ArrayList;
import java.util.List;

public class NotificationDispatchService {

    private final NotificationRoutingService routingService;

    public NotificationDispatchService(NotificationRoutingService routingService) {
        if (routingService == null) {
            throw new IllegalArgumentException("Notification routing service cannot be null.");
        }

        this.routingService = routingService;
    }

    public NotificationResult dispatchNotification(NotificationRequest notificationRequest) {
        NotificationRequestValidator.validate(notificationRequest);

        List<NotificationChannelType> attemptedChannels = new ArrayList<>();

        try {
            NotificationChannel channel = routingService.resolveChannel(notificationRequest, attemptedChannels);
            return channel.send(notificationRequest, attemptedChannels);

        } catch (IllegalArgumentException exception) {
            return new NotificationResult(
                    notificationRequest.getNotificationId(),
                    notificationRequest.getTenantId(),
                    notificationRequest.getUserId(),
                    null,
                    false,
                    "Notification dispatch failed",
                    null,
                    attemptedChannels,
                    exception.getMessage()
            );
        }
    }
}