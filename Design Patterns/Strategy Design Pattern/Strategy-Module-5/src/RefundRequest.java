public class RefundRequest {
    private final double amount;
    private final String remarks;
    private final String customerId;
    private final String refundRequestId;
    private final String bankAccountNumber;
    private final String originalTransactionId;

    // constructor
    public RefundRequest(double amount, String customerId, String originalTransactionId,
                         String refundRequestId, String bankAccountNumber, String remarks) {
        this.amount = amount;
        this.remarks = remarks;
        this.customerId = customerId;
        this.refundRequestId = refundRequestId;
        this.bankAccountNumber = bankAccountNumber;
        this.originalTransactionId = originalTransactionId;
    }

    // getters
    public double getAmount() {
        return amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOriginalTransactionId() {
        return originalTransactionId;
    }

    public String getRefundRequestId() {
        return refundRequestId;
    }

    public String getBankAccountNumber() {
        return bankAccountNumber;
    }

    public String getRemarks() {
        return remarks;
    }
}