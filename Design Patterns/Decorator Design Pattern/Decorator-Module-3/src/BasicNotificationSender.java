public class BasicNotificationSender implements NotificationSender {

    @Override
    public void send(String message) {
        System.out.println("Sending notification: " + message);
    }

}