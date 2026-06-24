public interface RetryPolicy {
    RetryDecision evaluate(NotificationRequest request, SendResult sendResult);
}