public class SmsRetryPolicy implements RetryPolicy {

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
                    "No retry required because " + NotificationProvider.SMS + " notification was sent successfully"
            );
        }
        if (request.getPriority() == NotificationPriority.CRITICAL) {
            return new RetryDecision(
                    true,
                    3,
                    20,
                    NotificationPriority.CRITICAL + " priority " + NotificationProvider.SMS +
                            " notification failed, retry required"
            );
        }
        return new RetryDecision(
                false,
                0,
                0,
                NotificationProvider.SMS + " notification failed, but retry is not required for priority: "
                        + request.getPriority()
        );

    }
}
