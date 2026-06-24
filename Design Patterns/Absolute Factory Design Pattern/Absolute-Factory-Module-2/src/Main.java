import java.util.Map;

public class Main {

    public static void main(String[] args) {

        NotificationProviderFactorySelector selector =
                new NotificationProviderFactorySelector();

        NotificationDispatchService dispatchService =
                new NotificationDispatchService(selector);

        System.out.println("Abstract Factory Module 2 — Notification Provider Suite");

        // ==================================================
        // TEST 1: EMAIL SUCCESS
        // ==================================================

        NotificationRequest emailSuccessRequest = new NotificationRequest(
                "NOTIF-101",
                NotificationProvider.EMAIL,
                NotificationType.TRANSACTIONAL,
                new RecipientDetails(
                        "CUST-101",
                        "customer101@gmail.com",
                        null,
                        null
                ),
                "PAYMENT_SUCCESS",
                NotificationPriority.HIGH,
                Map.of(
                        "userName", "Shashwat",
                        "amount", "1500",
                        "paymentId", "PAY-101"
                )
        );

        printResult(dispatchService.dispatch(emailSuccessRequest));

        // ==================================================
        // TEST 2: SMS SUCCESS
        // ==================================================

        NotificationRequest smsSuccessRequest = new NotificationRequest(
                "NOTIF-102",
                NotificationProvider.SMS,
                NotificationType.OTP,
                new RecipientDetails(
                        "CUST-102",
                        null,
                        "9876543210",
                        "+91"
                ),
                "LOGIN_OTP",
                NotificationPriority.CRITICAL,
                Map.of(
                        "userName", "Shashwat",
                        "otp", "123456"
                )
        );

        printResult(dispatchService.dispatch(smsSuccessRequest));

        // ==================================================
        // TEST 3: WHATSAPP SUCCESS
        // ==================================================

        NotificationRequest whatsappSuccessRequest = new NotificationRequest(
                "NOTIF-103",
                NotificationProvider.WHATSAPP,
                NotificationType.SECURITY_ALERT,
                new RecipientDetails(
                        "CUST-103",
                        null,
                        "9876543211",
                        "+91"
                ),
                "LOGIN_ALERT",
                NotificationPriority.HIGH,
                Map.of(
                        "userName", "Shashwat",
                        "location", "Chennai",
                        "time", "10:30 PM"
                )
        );

        printResult(dispatchService.dispatch(whatsappSuccessRequest));

        // ==================================================
        // TEST 4: INVALID EMAIL FAILURE
        // Expected:
        // success = false
        // deliveryStatus = FAILED
        // providerMessageId = null
        // failureReason = Invalid email address for EMAIL notification
        // ==================================================

        NotificationRequest invalidEmailRequest = new NotificationRequest(
                "NOTIF-104",
                NotificationProvider.EMAIL,
                NotificationType.TRANSACTIONAL,
                new RecipientDetails(
                        "CUST-104",
                        "invalid-email",
                        null,
                        null
                ),
                "PAYMENT_FAILED",
                NotificationPriority.HIGH,
                Map.of(
                        "userName", "Shashwat",
                        "paymentId", "PAY-404"
                )
        );

        printResult(dispatchService.dispatch(invalidEmailRequest));

        // ==================================================
        // TEST 5: EMAIL SEND FAILURE WITH RETRY
        // This will work only if EmailNotificationSender checks body contains FAIL_SEND.
        //
        // Expected:
        // success = false
        // deliveryStatus = RETRY_SCHEDULED
        // providerMessageId = null
        // retryRequired = true
        // maxAttempts = 5 because priority is CRITICAL
        // ==================================================

        NotificationRequest emailRetryRequest = new NotificationRequest(
                "NOTIF-105",
                NotificationProvider.EMAIL,
                NotificationType.SECURITY_ALERT,
                new RecipientDetails(
                        "CUST-105",
                        "customer105@gmail.com",
                        null,
                        null
                ),
                "FAIL_SEND_SECURITY_ALERT",
                NotificationPriority.CRITICAL,
                Map.of(
                        "userName", "Shashwat",
                        "reason", "FAIL_SEND"
                )
        );

        printResult(dispatchService.dispatch(emailRetryRequest));
    }

    private static void printResult(NotificationDispatchResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Notification ID: " + result.getNotificationId());
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Delivery Status: " + result.getDeliveryStatus());
        System.out.println("Provider Message ID: " + result.getProviderMessageId());
        System.out.println("Message: " + result.getMessage());

        if (result.getRetryDecision() != null) {
            System.out.println("Retry Required: " + result.getRetryDecision().getShouldRetry());
            System.out.println("Max Attempts: " + result.getRetryDecision().getMaxAttempts());
            System.out.println("Retry Delay Seconds: " + result.getRetryDecision().getDelayInSeconds());
            System.out.println("Retry Reason: " + result.getRetryDecision().getReason());
        }

        System.out.println("Failure Reason: " + result.getFailureReason());
        System.out.println("==================================================");
    }
}