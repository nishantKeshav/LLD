import java.util.List;

public class Main {
    public static void main(String[] args) {
        String message = "This is a test message";

        List<NotificationStrategy> strategies = List.of(
                new EmailNotificationStrategy(),
                new SmsNotificationStrategy(),
                new WhatsAppNotificationStrategy()
        );

        NotificationService notificationService = new NotificationService(strategies);

        NotificationType[] notificationTypes = NotificationType.values();
        for (NotificationType notificationType : notificationTypes) {
            notificationService.sendNotification(notificationType, message);
        }

    }
}