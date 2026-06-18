public class RefundRequestValidator {

    public void validateCommonFields(RefundRequest refundRequest) {
        if (refundRequest.getAmount() <= 0 || refundRequest.getAmount() > 50000) {
            throw new IllegalArgumentException("Amount must be greater than zero and less than 50000");
        }

        if (isBlank(refundRequest.getCustomerId())) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank");
        }

        if (isBlank(refundRequest.getRefundRequestId())) {
            throw new IllegalArgumentException("Refund Request ID cannot be null or blank");
        }

        if (isBlank(refundRequest.getRemarks())) {
            throw new IllegalArgumentException("Remarks cannot be null or blank");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}