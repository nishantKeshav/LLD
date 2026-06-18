public class EncryptionNotificationDecorator extends NotificationSenderDecorator {

    public EncryptionNotificationDecorator(NotificationSender wrappedSender) {
        super(wrappedSender);
    }

    @Override
    public void send(String message) {
        String encryptedMessage = encrypt(message);
        wrappedSender.send(encryptedMessage);
    }

    private String encrypt(String message) {
        // Simple encryption logic (for demonstration purposes)
        return "[ENCRYPTED : " + message + "]";
    }
}
