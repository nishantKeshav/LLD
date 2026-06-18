import java.time.LocalDateTime;

public class WhatsappNotificationSender implements NotificationSender {

    @Override
    public void send(String recipient, String message) {
        if (recipient == null || recipient.isBlank()) {
            throw new IllegalArgumentException("Recipient is null or blank");
        }
        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message is null or blank");
        }
        System.out.println("Sending WhatsApp Message to " + recipient + ": " + message + " at " + LocalDateTime.now());
    }
}
