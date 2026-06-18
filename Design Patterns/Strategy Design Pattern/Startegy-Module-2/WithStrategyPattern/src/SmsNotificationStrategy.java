public class SmsNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.SMS;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message + " using " + getNotificationType());
    }
}
