public class ReportRow {

    private final String rowId;
    private final double amount;
    private final String status;
    private final String customerId;
    private final String customerName;

    public ReportRow(String rowId, String customerId, String customerName, double amount, String status) {
        if (rowId == null || rowId.isBlank()) {
            throw new IllegalArgumentException("rowId can not be null or blank");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("customerId can not be null or blank");
        }
        if (customerName == null || customerName.isBlank()) {
            throw new IllegalArgumentException("customerName can not be null or blank");
        }
        if (amount <= 0) {
            throw new IllegalArgumentException("amount can not be equal or less than zero");
        }
        if (status == null || status.isBlank()) {
            throw new IllegalArgumentException("status can not be null or blank");
        }
        this.rowId = rowId;
        this.amount = amount;
        this.status = status;
        this.customerId = customerId;
        this.customerName = customerName;
    }

    public String getRowId() {
        return rowId;
    }

    public double getAmount() {
        return amount;
    }

    public String getStatus() {
        return status;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCustomerName() {
        return customerName;
    }
}
