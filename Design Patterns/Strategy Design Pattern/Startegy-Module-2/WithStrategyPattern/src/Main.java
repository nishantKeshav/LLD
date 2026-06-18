import java.util.List;

public class Main {
    public static void main(String[] args) {
        String message = "This is a test message";

        List<NotificationStrategy> strategies = List.of(
                new EmailNotificationStrategy(),
                new SmsNotificationStrategy(),
                new WhatsAppNotificationStrategy()
        );

        for (NotificationStrategy strategy : strategies) {
            sendNotificationTestMethod(message, strategy);
        }
    }

    private static void sendNotificationTestMethod(String message, NotificationStrategy notificationStrategy) {
        NotificationService notificationService = new NotificationService(notificationStrategy);
        notificationService.sendNotification(message);
    }
}