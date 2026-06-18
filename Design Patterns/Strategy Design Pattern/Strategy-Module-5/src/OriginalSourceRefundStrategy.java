public class OriginalSourceRefundStrategy implements RefundStrategy {

    @Override
    public RefundMode getRefundMode() {
        return RefundMode.ORIGINAL_SOURCE;
    }

    @Override
    public void refund(RefundRequest refundRequest) {
        validate(refundRequest);

        System.out.println("Validating original transaction: " + refundRequest.getOriginalTransactionId());
        System.out.println("Refund request: " + refundRequest.getRefundRequestId());
        System.out.println("Refunding " + refundRequest.getAmount() + " to original payment source");
        System.out.println("Customer: " + refundRequest.getCustomerId());
        System.out.println("Remarks: " + refundRequest.getRemarks());
    }

    private void validate(RefundRequest refundRequest) {
        if (refundRequest.getOriginalTransactionId() == null ||
                refundRequest.getOriginalTransactionId().isBlank()) {
            throw new IllegalArgumentException("Original transaction ID is required");
        }
    }
}