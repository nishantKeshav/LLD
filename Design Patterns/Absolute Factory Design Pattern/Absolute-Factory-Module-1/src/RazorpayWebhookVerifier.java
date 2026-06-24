public class RazorpayWebhookVerifier implements WebhookVerifier{

    @Override
    public WebhookVerificationResult verify(WebhookPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Payload can't be null");
        }
        if (payload.getProvider() != PaymentProvider.RAZORPAY) {
            return new WebhookVerificationResult(
                    PaymentProvider.RAZORPAY,
                    false,
                    "Invalid webhook provider for verifier " + PaymentProvider.RAZORPAY,
                    payload.getEventType()
            );
        }
        if (payload.getSignature() == null || payload.getSignature().isBlank()) {
            return new WebhookVerificationResult(
                    PaymentProvider.RAZORPAY,
                    false,
                    "webhook signature is missing for verifier " + PaymentProvider.RAZORPAY,
                    payload.getEventType()
            );
        }
        if (payload.getPayload() == null || payload.getPayload().isBlank()) {
            return new WebhookVerificationResult(
                    PaymentProvider.RAZORPAY,
                    false,
                    "webhook payload is missing for verifier " + PaymentProvider.RAZORPAY,
                    payload.getEventType()
            );
        }
        if (payload.getEventType() ==  null || payload.getEventType().isBlank()) {
            return new WebhookVerificationResult(
                    PaymentProvider.RAZORPAY,
                    false,
                    "webhook payload is missing for verifier " + PaymentProvider.RAZORPAY,
                    null
            );
        }
        return new WebhookVerificationResult(
                PaymentProvider.RAZORPAY,
                true,
                "Webhook Verified successfully for " + PaymentProvider.RAZORPAY,
                payload.getEventType()
        );
    }
}
