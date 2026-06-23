import java.util.Set;
import java.util.List;

public interface NotificationChannel {

    String getChannelName();

    NotificationChannelType getChannelType();

    Set<NotificationPriority> supportedPriorities();

    boolean supportsPriority(NotificationPriority priority);

    NotificationResult send(NotificationRequest request, List<NotificationChannelType> attemptedChannels);

}