public class NotificationChannelFactory {

    private final NotificationChannelRegistry registry;

    public NotificationChannelFactory(NotificationChannelRegistry registry) {
        if (registry == null) {
            throw new NullPointerException("Notification channel registry is null");
        }
        this.registry = registry;
    }

    public NotificationChannel getChannel(NotificationChannelType channelType) {
        return registry.get(channelType);
    }
}
