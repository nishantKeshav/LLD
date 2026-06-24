import java.time.LocalDateTime;

public class RefundRequest {

    private final String refundId;
    private final String paymentId;
    private final double amount;
    private final String reason;
    private final LocalDateTime refundTimeStamp;

    public RefundRequest(String refundId, String paymentId, double amount, String reason) {
        if (refundId == null || refundId.isBlank()) {
            throw new IllegalArgumentException("refundId cannot be null or blank");
        }
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("paymentId cannot be null or blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount must be grater than zero");
        }
        if (reason == null) {
            throw new IllegalArgumentException("reason cannot be null");
        }
        this.refundId = refundId;
        this.paymentId = paymentId;
        this.amount = amount;
        this.reason = reason;
        refundTimeStamp= LocalDateTime.now();
    }

    public String getRefundId() {
        return refundId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public double getAmount() {
        return amount;
    }

    public String getReason() {
        return reason;
    }

    public LocalDateTime getRefundTimeStamp() {
        return refundTimeStamp;
    }

}
