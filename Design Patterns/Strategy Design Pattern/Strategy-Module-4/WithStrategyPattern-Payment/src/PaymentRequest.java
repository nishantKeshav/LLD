public class PaymentRequest {
    private double amount;
    private String customerId;
    private String transactionId;
    private String remarks;

    public PaymentRequest(double amount, String customerId, String transactionId, String remarks) {
        this.amount = amount;
        this.remarks = remarks;
        this.customerId = customerId;
        this.transactionId = transactionId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

}