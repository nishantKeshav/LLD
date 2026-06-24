public class EmailRetryPolicy implements RetryPolicy{

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
                    "No retry required because email notification was sent successfully"
            );
        }
        if (request.getPriority() == NotificationPriority.CRITICAL) {
            return new RetryDecision(
                    true,
                    5,
                    30,
                    NotificationPriority.CRITICAL + " priority " + NotificationProvider.EMAIL +
                            " notification failed, retry required"
            );
        }
        if (request.getPriority() == NotificationPriority.HIGH) {
            return new RetryDecision(
                    true,
                    3,
                    60,
                    NotificationPriority.HIGH + " priority " + NotificationProvider.EMAIL +
                            " notification failed, retry required"
            );
        }
        return new RetryDecision(
                false,
                0,
                0,
                NotificationProvider.EMAIL + " notification failed, but retry is not required for priority: "
                        + request.getPriority()
        );
    }
}
