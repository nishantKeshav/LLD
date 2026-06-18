public class Main {
    public static void main(String[] args) {
        //TIP Press <shortcut actionId="ShowIntentionActions"/> with your caret at the highlighted text
        // to see how IntelliJ IDEA suggests fixing it.
        System.out.println("Without Strategy Pattern");
        String[] channels = {"EMAIL", "SMS", "WHATSAPP", "OTHER"};
        NotificationService notificationService = new NotificationService();
        for (String channel : channels) {
            notificationService.sendNotification(channel, "Hello, this is a test message!");
        }
    }
}