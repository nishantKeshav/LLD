public interface NotificationStrategy {
    NotificationType getNotificationType();
    void sendNotification(String message);
}
