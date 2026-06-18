public class NotificationService {

    public void sendNotification(String channel, String message) {
        if (channel.equals("EMAIL")) {
            System.out.println("Sending EMAIL: " + message);
        } else if (channel.equals("SMS")) {
            System.out.println("Sending SMS: " + message);
        } else if (channel.equals("WHATSAPP")) {
            System.out.println("Sending WHATSAPP: " + message);
        } else {
            System.out.println("Invalid channel");
        }
    }
}
