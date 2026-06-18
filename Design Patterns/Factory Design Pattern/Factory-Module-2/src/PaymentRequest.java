public class PaymentRequest {

    private final String paymentId;
    private final String customerId;
    private final double amount;
    private final String currency;
    private final PaymentMode paymentMode;
    private final String description;

    public PaymentRequest(
            String paymentId,
            String customerId,
            double amount,
            String currency,
            PaymentMode paymentMode,
            String description
    ) {
        if (paymentId == null || paymentId.isEmpty()) {
            throw new IllegalArgumentException("Payment Id is null");
        }
        if (customerId == null || customerId.isEmpty()) {
            throw new IllegalArgumentException("Customer Id is null");
        }
        if (amount <= 0) {
            throw new NullPointerException("Amount is negative");
        }
        if (currency == null || currency.isEmpty()) {
            throw new NullPointerException("Currency is null");
        }
        if (paymentMode == null) {
            throw new NullPointerException("Payment Mode is null");
        }
        if (description == null || description.isEmpty()) {
            throw new NullPointerException("Description is null");
        }
        this.paymentId = paymentId;
        this.customerId = customerId;
        this.amount = amount;
        this.currency = currency;
        this.paymentMode = paymentMode;
        this.description = description;
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

    public String getCurrency() {
        return currency;
    }

    public PaymentMode getPaymentMode() {
        return paymentMode;
    }

    public String getDescription() {
        return description;
    }
}