import java.util.UUID;

public class EmailNotificationSender implements NotificationSender {

    @Override
    public SendResult send(RenderedNotification renderedNotification) {
        if (renderedNotification == null) {
            throw new IllegalArgumentException("Rendered notification cannot be null");
        }

        if (renderedNotification.getBody() != null
                && renderedNotification.getBody().contains("FAIL_SEND")) {

            return new SendResult(
                    renderedNotification.getNotificationId(),
                    NotificationProvider.EMAIL,
                    false,
                    null,
                    "Email notification send failed",
                    "Simulated EMAIL provider failure"
            );
        }

        return new SendResult(
                renderedNotification.getNotificationId(),
                NotificationProvider.EMAIL,
                true,
                "EMAIL-" + renderedNotification.getNotificationId() + "-" + UUID.randomUUID(),
                "Email notification sent successfully",
                null
        );
    }
}