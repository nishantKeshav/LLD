public class RazorpayPaymentProviderFactory implements PaymentProviderFactory {

    @Override
    public PaymentClient createPaymentClient() {
        return new RazorpayPaymentClient();
    }

    @Override
    public RefundClient createRefundClient() {
        return new RazorpayRefundClient();
    }

    @Override
    public PaymentStatusClient createPaymentStatusClient() {
        return new RazorpayStatusClient();
    }

    @Override
    public WebhookVerifier createWebhookVerifier() {
        return new RazorpayWebhookVerifier();
    }
}