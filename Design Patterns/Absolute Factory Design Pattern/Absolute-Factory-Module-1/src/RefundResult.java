public class RefundResult {

    private final String refundId;
    private final String paymentId;
    private final PaymentProvider provider;
    private final boolean success;
    private final String message;
    private final String providerRefundReferenceId;

    public RefundResult (String refundId, String paymentId, PaymentProvider provider,
                         boolean success, String message, String providerRefundReferenceId) {
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.provider = provider;
        this.success = success;
        this.message = message;
        this.providerRefundReferenceId = providerRefundReferenceId;
    }

    public String getRefundId() {
        return refundId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getProviderRefundReferenceId() {
        return providerRefundReferenceId;
    }
}
