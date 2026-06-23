import java.util.Set;
import java.util.List;
import java.util.UUID;

public class WhatsappNotificationChannel implements NotificationChannel {

    @Override
    public String getChannelName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public NotificationChannelType getChannelType() {
        return NotificationChannelType.WHATSAPP;
    }

    @Override
    public Set<NotificationPriority> supportedPriorities() {
        return Set.of(NotificationPriority.NORMAL, NotificationPriority.HIGH);
    }

    @Override
    public boolean supportsPriority(NotificationPriority priority) {
        return supportedPriorities().contains(priority);
    }

    @Override
    public NotificationResult send(NotificationRequest request, List<NotificationChannelType> attemptedChannels) {
        return new NotificationResult(
                request.getNotificationId(),
                request.getTenantId(),
                request.getUserId(),
                getChannelType(),
                true,
                getChannelType().toString() + " notification sent successfully",
                getChannelType().toString() + "-" + UUID.randomUUID(),
                attemptedChannels,
                null
        );
    }
}
