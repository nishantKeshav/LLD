public class PaymentResponse {

    private final String paymentId;
    private final boolean success;
    private final String message;

    public PaymentResponse(String paymentId, boolean success, String message) {
        this.paymentId = paymentId;
        this.success = success;
        this.message = message;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }
}
