import java.util.Map;
import java.util.EnumMap;

public class NotificationChannelRegistry {

    private final Map<NotificationChannelType, NotificationChannel> channels;

    public NotificationChannelRegistry() {
        channels =  new EnumMap<>(NotificationChannelType.class);
        register(new EmailNotificationChannel());
        register(new SmsNotificationChannel());
        register(new WhatsappNotificationChannel());
        register(new PushNotificationChannel());
    }

    public void register(NotificationChannel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Notification channel cannot be null.");
        }
        channels.put(channel.getChannelType(), channel);
    }

    public NotificationChannel get(NotificationChannelType channelType) {
        if (channelType == null) {
            throw new IllegalArgumentException("Notification channel type cannot be null.");
        }
        NotificationChannel channel = channels.get(channelType);
        if (channel == null) {
            throw new IllegalArgumentException("No notification channel registered for " + channelType);
        }
        return channel;
    }

    public boolean isRegistered(NotificationChannelType channelType) {
        return channels.containsKey(channelType);
    }

}
