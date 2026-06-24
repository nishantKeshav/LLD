public class WhatsAppRetryPolicy implements RetryPolicy {

    @Override
    public RetryDecision evaluate(NotificationRequest request, SendResult sendResult) {
        if (request == null) {
            throw new IllegalArgumentException("request cannot be null");
        }
        if (sendResult == null) {
            throw new IllegalArgumentException("sendResult cannot be null");
        }
        if (sendResult.isSuccess()) {
            return new RetryDecision(
                    false,
                    0,
                    0,
                    "No retry required because " + NotificationProvider.WHATSAPP + " notification was sent successfully"
            );
        }
        if (request.getPriority() == NotificationPriority.CRITICAL) {
            return new RetryDecision(
                    true,
                    4,
                    30,
                    NotificationPriority.CRITICAL + " priority " + NotificationProvider.WHATSAPP +
                            " notification failed, retry required"
            );
        }
        if (request.getPriority() == NotificationPriority.HIGH) {
            return new RetryDecision(
                    true,
                    2,
                    60,
                    NotificationPriority.CRITICAL + " priority " + NotificationProvider.WHATSAPP +
                            " notification failed, retry required"
            );
        }
        return new RetryDecision(
                false,
                0,
                0,
                NotificationProvider.WHATSAPP + " notification failed, but retry is not required for priority: "
                        + request.getPriority()
        );
    }
}
