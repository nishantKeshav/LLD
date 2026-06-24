public class NotificationProviderFactorySelector {

    public NotificationProviderFactory getFactory(NotificationProvider provider){
        if (provider == null) {
            throw new IllegalArgumentException("NotificationProvider cannot be null");
        }
        return switch (provider) {
            case SMS -> new SmsNotificationProviderFactory();
            case EMAIL -> new EmailNotificationProviderFactory();
            case WHATSAPP -> new WhatsAppNotificationProviderFactory();
            default -> throw new IllegalArgumentException("Unknown NotificationProvider " + provider);
        };
    }
}
