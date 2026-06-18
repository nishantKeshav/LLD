public class NotificationSenderFactory {

    private NotificationSenderFactory() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public static NotificationSender getInstance(NotificationType notificationType) {
        if (notificationType == null) {
            throw new NullPointerException("NotificationType cannot be null");
        }
        return switch (notificationType) {
            case SMS -> new SmsNotificationSender();
            case EMAIL -> new EmailNotificationSender();
            case WHATSAPP -> new WhatsappNotificationSender();
            default -> throw new UnsupportedOperationException("Not supported yet.");
        };
    }
}
