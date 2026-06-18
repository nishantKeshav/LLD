public class LoggingNotificationDecorator extends NotificationSenderDecorator {

    public LoggingNotificationDecorator(NotificationSender wrappedSender) {
        super(wrappedSender);
    }

    @Override
    public void send(String message) {
        System.out.println("LOG: Before sending notification");
        wrappedSender.send(message);
        System.out.println("LOG: After sending notification");
    }
}
