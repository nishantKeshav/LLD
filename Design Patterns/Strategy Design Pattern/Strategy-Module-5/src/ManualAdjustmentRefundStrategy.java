public class ManualAdjustmentRefundStrategy implements RefundStrategy {

    @Override
    public RefundMode getRefundMode() {
        return RefundMode.MANUAL_ADJUSTMENT;
    }

    @Override
    public void refund(RefundRequest refundRequest) {
        System.out.println("Creating manual adjustment entry");
        System.out.println("Refund request: " + refundRequest.getRefundRequestId());
        System.out.println("Amount: " + refundRequest.getAmount());
        System.out.println("Customer: " + refundRequest.getCustomerId());
        System.out.println("Remarks: " + refundRequest.getRemarks());
    }
}
