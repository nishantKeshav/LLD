public interface WebhookVerifier {
    WebhookVerificationResult verify(WebhookPayload payload);
}