public class WhatsAppNotificationStrategy implements NotificationStrategy {

    @Override
    public NotificationType getNotificationType() {
        return NotificationType.WHATSAPP;
    }

    @Override
    public void sendNotification(String message) {
        System.out.println("Sending notification: " + message + " using " + getNotificationType());
    }
}
