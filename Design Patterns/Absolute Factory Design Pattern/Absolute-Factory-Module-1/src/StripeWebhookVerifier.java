public class StripeWebhookVerifier implements WebhookVerifier{

    @Override
    public WebhookVerificationResult verify(WebhookPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload can't be null");
        }
        if (payload.getProvider() != PaymentProvider.STRIPE) {
            return new WebhookVerificationResult(
                    PaymentProvider.STRIPE,
                    false,
                    "Invalid webhook provider for verifier " + PaymentProvider.STRIPE,
                    payload.getEventType()
            );
        }
        if (payload.getSignature() == null || payload.getSignature().isBlank()) {
            return new WebhookVerificationResult(
                    PaymentProvider.STRIPE,
                    false,
                    "webhook signature is missing for verifier " + PaymentProvider.STRIPE,
                    payload.getEventType()
            );
        }
        if (payload.getPayload() == null || payload.getPayload().isBlank()) {
            return new WebhookVerificationResult(
                    PaymentProvider.STRIPE,
                    false,
                    "webhook payload is missing for verifier " + PaymentProvider.STRIPE,
                    payload.getEventType()
            );
        }
        if (payload.getEventType() ==  null || payload.getEventType().isBlank()) {
            return new WebhookVerificationResult(
                    PaymentProvider.STRIPE,
                    false,
                    "webhook payload is missing for verifier " + PaymentProvider.STRIPE,
                    null
            );
        }
        return new WebhookVerificationResult(
                PaymentProvider.STRIPE,
                true,
                "Webhook Verified successfully for " + PaymentProvider.STRIPE,
                payload.getEventType()
        );
    }

}
