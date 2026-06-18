public class PaymentRequest {

    private final double amount;
    private final String currency;
    private final String paymentId;
    private final String customerId;
    private final String paymentMode;

    public PaymentRequest(String paymentId, String customerId, double amount, String paymentMode, String currency) {
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.paymentMode = paymentMode;
        this.currency = currency;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getAmount() {
        return amount;
    }

    public String getPaymentMode() {
        return paymentMode;
    }

    public String getCurrency() {
        return currency;
    }

}
