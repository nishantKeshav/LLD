public class MetricsNotificationDecorator extends NotificationSenderDecorator {

    private final NotificationMetrics metrics;

    public MetricsNotificationDecorator(NotificationSender notificationSender, NotificationMetrics metrics) {
        super(notificationSender);
        this.metrics = metrics;
    }

    @Override
    public void send(String message) {
        super.send(message);
        metrics.incrementSentCount();
    }
}