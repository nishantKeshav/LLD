import java.util.List;

public class Main {

    public static void main(String[] args) {
        NotificationChannelRegistry registry = new NotificationChannelRegistry();
        NotificationChannelFactory factory = new NotificationChannelFactory(registry);
        TenantNotificationConfigService configService = new TenantNotificationConfigService();
        NotificationRoutingService routingService = new NotificationRoutingService(factory, configService);
        NotificationDispatchService dispatchService = new NotificationDispatchService(routingService);

        NotificationRequest criticalSmsRequest = new NotificationRequest(
                "NOTIF-101",
                "TENANT-A",
                "USER-101",
                "user101@example.com",
                "9876543210",
                "device-token-101",
                "Payment Alert",
                "Your payment was successful.",
                NotificationPriority.CRITICAL,
                NotificationChannelType.SMS,
                List.of(NotificationChannelType.EMAIL, NotificationChannelType.PUSH)
        );

        printResult(dispatchService.dispatchNotification(criticalSmsRequest));

        NotificationRequest fallbackToEmailRequest = new NotificationRequest(
                "NOTIF-102",
                "TENANT-B",
                "USER-102",
                "user102@example.com",
                "9876543211",
                "device-token-102",
                "Security Alert",
                "New login detected.",
                NotificationPriority.CRITICAL,
                NotificationChannelType.SMS,
                List.of(NotificationChannelType.EMAIL)
        );

        printResult(dispatchService.dispatchNotification(fallbackToEmailRequest));

        NotificationRequest fallbackToPushRequest = new NotificationRequest(
                "NOTIF-103",
                "TENANT-C",
                "USER-103",
                "user103@example.com",
                "9876543212",
                "device-token-103",
                "Offer Alert",
                "New offer available.",
                NotificationPriority.LOW,
                NotificationChannelType.EMAIL,
                List.of(NotificationChannelType.PUSH)
        );

        printResult(dispatchService.dispatchNotification(fallbackToPushRequest));

        NotificationRequest failureRequest = new NotificationRequest(
                "NOTIF-104",
                "TENANT-C",
                "USER-104",
                "user104@example.com",
                "9876543213",
                "device-token-104",
                "Critical Alert",
                "Suspicious activity detected.",
                NotificationPriority.CRITICAL,
                NotificationChannelType.PUSH,
                List.of()
        );

        printResult(dispatchService.dispatchNotification(failureRequest));
    }

    private static void printResult(NotificationResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Notification ID: " + result.getNotificationId());
        System.out.println("Tenant ID: " + result.getTenantId());
        System.out.println("User ID: " + result.getUserId());
        System.out.println("Sent: " + result.isSent());
        System.out.println("Channel Used: " + result.getChannelUsed());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Provider Message ID: " + result.getProviderMessageId());
        System.out.println("Attempted Channels: " + result.getAttemptedChannels());
        System.out.println("Failure Reason: " + result.getFailureReason());
        System.out.println("==================================================");
    }
}