public class PaymentStatusRequest {

    private final String paymentId;

    public PaymentStatusRequest(String paymentId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId cannot be null or blank");
        }
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
