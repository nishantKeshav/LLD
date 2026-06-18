public class Main {
    public static void main(String[] args) {
        System.out.println("Module 3 — Notification Sender Decorator");

        NotificationMetrics metrics = new NotificationMetrics();

        NotificationSender sender = new BasicNotificationSender();

        sender = new LoggingNotificationDecorator(sender);
        sender = new EncryptionNotificationDecorator(sender);
        sender = new TimestampNotificationDecorator(sender);
        sender = new MetricsNotificationDecorator(sender, metrics);

        sender.send("Payment successful for order ORD-101");

        System.out.println("Total notifications sent: " + metrics.getSentCount());
    }
}