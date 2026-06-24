import java.util.UUID;

public class WhatsAppNotificationSender implements NotificationSender{

    @Override
    public SendResult send(RenderedNotification renderedNotification) {
        return new SendResult(
                renderedNotification.getNotificationId(),
                renderedNotification.getProvider(),
                true,
                NotificationProvider.WHATSAPP + "-" + renderedNotification.getNotificationId() + "-" + UUID.randomUUID(),
                NotificationProvider.WHATSAPP + "Notification sent Successfully",
                null
        );
    }
}
