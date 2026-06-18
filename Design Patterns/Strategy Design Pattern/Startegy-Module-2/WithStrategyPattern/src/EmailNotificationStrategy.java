public class EmailNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.EMAIL;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message + " using " + getNotificationType());
    }
}
