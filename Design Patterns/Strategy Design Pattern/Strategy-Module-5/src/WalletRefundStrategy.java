public class WalletRefundStrategy implements RefundStrategy {

    @Override
    public RefundMode getRefundMode() {
        return RefundMode.WALLET;
    }

    @Override
    public void refund(RefundRequest refundRequest) {
        System.out.println("Checking wallet for customer: " + refundRequest.getCustomerId());
        System.out.println("Refund request: " + refundRequest.getRefundRequestId());
        System.out.println("Adding " + refundRequest.getAmount() + " to customer wallet");
        System.out.println("Remarks: " + refundRequest.getRemarks());
    }
}
