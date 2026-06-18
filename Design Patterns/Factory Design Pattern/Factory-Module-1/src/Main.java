public class Main {
    public static void main() {
        System.out.println("Factory Module 1 — Notification Sender Factory");

        NotificationSender emailSender = NotificationSenderFactory.getInstance(NotificationType.EMAIL);
        emailSender.send("amit@example.com", "Welcome to the Factory Pattern module");

        NotificationSender smsSender = NotificationSenderFactory.getInstance(NotificationType.SMS);
        smsSender.send("9876543210", "Your OTP is 123456");

        NotificationSender whatsappSender = NotificationSenderFactory.getInstance(NotificationType.WHATSAPP);
        whatsappSender.send("9876543210", "Your order has been shipped");
    }
}