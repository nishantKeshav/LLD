import java.time.LocalDateTime;

public class TimestampNotificationDecorator extends NotificationSenderDecorator {

    public TimestampNotificationDecorator(NotificationSender wrappedSender) {
        super(wrappedSender);
    }

    @Override
    public void send(String message) {
        String timestampedMessage = addTimestamp(message);
        wrappedSender.send(timestampedMessage);
    }

    private String addTimestamp(String message) {
        return "[" + LocalDateTime.now() + "] " + message;
    }
}
