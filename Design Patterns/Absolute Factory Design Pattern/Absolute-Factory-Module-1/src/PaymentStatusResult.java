public class PaymentStatusResult {

    private final String paymentId;
    private final PaymentProvider provider;
    private final PaymentStatus status;
    private final String message;

    public PaymentStatusResult(String paymentId, PaymentProvider provider, PaymentStatus status, String message) {
        this.paymentId = paymentId;
        this.provider = provider;
        this.status = status;
        this.message = message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
