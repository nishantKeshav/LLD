import java.util.UUID;

public class SmsNotificationSender implements NotificationSender {

    @Override
    public SendResult send(RenderedNotification renderedNotification) {
        return new SendResult(
                renderedNotification.getNotificationId(),
                renderedNotification.getProvider(),
                true,
                NotificationProvider.SMS + "-" + renderedNotification.getNotificationId() + "-" + UUID.randomUUID(),
                NotificationProvider.SMS + "Notification sent Successfully",
                null
        );
    }
}
