public interface PaymentProviderFactory {
    PaymentClient createPaymentClient();
    RefundClient createRefundClient();
    PaymentStatusClient createPaymentStatusClient();
    WebhookVerifier createWebhookVerifier();
}