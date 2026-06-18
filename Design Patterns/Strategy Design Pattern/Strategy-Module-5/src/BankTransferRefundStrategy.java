public class BankTransferRefundStrategy implements RefundStrategy {

    @Override
    public RefundMode getRefundMode() {
        return RefundMode.BANK_TRANSFER;
    }

    @Override
    public void refund(RefundRequest refundRequest) {
        validate(refundRequest);

        System.out.println("Validating bank account: " + refundRequest.getBankAccountNumber());
        System.out.println("Refund request: " + refundRequest.getRefundRequestId());
        System.out.println("Transferring " + refundRequest.getAmount() + " to bank account");
        System.out.println("Customer: " + refundRequest.getCustomerId());
        System.out.println("Remarks: " + refundRequest.getRemarks());
    }

    private void validate(RefundRequest refundRequest) {
        if (refundRequest.getBankAccountNumber() == null ||
                refundRequest.getBankAccountNumber().isBlank()) {
            throw new IllegalArgumentException("Bank account number is required");
        }
    }
}