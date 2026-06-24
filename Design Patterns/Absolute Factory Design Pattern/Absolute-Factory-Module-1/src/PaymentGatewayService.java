public class PaymentGatewayService {

    public PaymentResult processPayment(PaymentProvider provider, PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }
        PaymentProviderFactory factory = PaymentProviderFactorySelector.getFactory(provider);
        return factory.createPaymentClient().processPayment(request);
    }

    public RefundResult processRefund(PaymentProvider provider, RefundRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Refund request cannot be null.");
        }
        PaymentProviderFactory factory = PaymentProviderFactorySelector.getFactory(provider);
        return factory.createRefundClient().processRefund(request);
    }

    public PaymentStatusResult checkStatus(PaymentProvider provider, PaymentStatusRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment status request cannot be null.");
        }
        PaymentProviderFactory factory = PaymentProviderFactorySelector.getFactory(provider);
        return factory.createPaymentStatusClient().checkStatus(request);
    }

    public WebhookVerificationResult verifyWebhook(PaymentProvider provider, WebhookPayload payload) {
        if (payload == null) {
            throw new IllegalArgumentException("Webhook payload cannot be null.");
        }
        PaymentProviderFactory factory = PaymentProviderFactorySelector.getFactory(provider);
        return factory.createWebhookVerifier().verify(payload);
    }
}