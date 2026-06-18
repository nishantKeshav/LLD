public abstract class NotificationSenderDecorator implements NotificationSender {

    protected final NotificationSender wrappedSender;

    protected NotificationSenderDecorator(NotificationSender wrappedSender) {
        if (wrappedSender == null) {
            throw new IllegalArgumentException("Wrapped sender cannot be null");
        }
        this.wrappedSender = wrappedSender;
    }

    @Override
    public void send(String message) {
        wrappedSender.send(message);
    }
}