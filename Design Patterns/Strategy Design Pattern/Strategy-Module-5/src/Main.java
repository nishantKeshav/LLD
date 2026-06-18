import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("With Strategy Pattern - Refund Service");
        List<RefundStrategy> strategies = List.of(
                new OriginalSourceRefundStrategy(),
                new WalletRefundStrategy(),
                new BankTransferRefundStrategy(),
                new ManualAdjustmentRefundStrategy()
        );

        RefundService refundService = new RefundService(strategies);

        RefundRequest refundRequest = new RefundRequest(
                500.00,
                "CUST-101",
                "TXN-9001",
                "REF-7001",
                "XXXXXX1234",
                "Customer requested refund"
        );

        for (RefundMode refundMode : RefundMode.values()) {
            refundService.initiateRefund(refundMode, refundRequest);
            System.out.println();
        }
    }
}