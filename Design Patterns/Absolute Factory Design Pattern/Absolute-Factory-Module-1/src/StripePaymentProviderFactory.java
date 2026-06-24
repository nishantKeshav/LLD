public class StripePaymentProviderFactory implements PaymentProviderFactory {

    @Override
    public PaymentClient createPaymentClient() {
        return new StripePaymentClient();
    }

    @Override
    public RefundClient createRefundClient() {
        return new StripeRefundClient();
    }

    @Override
    public PaymentStatusClient createPaymentStatusClient() {
        return new StripeStatusClient();
    }

    @Override
    public WebhookVerifier createWebhookVerifier() {
        return new StripeWebhookVerifier();
    }
}