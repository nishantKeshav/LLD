public interface RefundStrategy {
    RefundMode getRefundMode();
    void refund(RefundRequest refundRequest);
}
