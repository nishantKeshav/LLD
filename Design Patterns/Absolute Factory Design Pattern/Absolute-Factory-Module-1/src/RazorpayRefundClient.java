import java.util.UUID;

public class RazorpayRefundClient implements RefundClient{

    @Override
    public RefundResult processRefund(RefundRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }
        System.out.println("Refund processed through " + PaymentProvider.RAZORPAY);
        return new RefundResult(
                request.getRefundId(),
                request.getPaymentId(),
                PaymentProvider.RAZORPAY,
                true,
                "Refund Successful through " + PaymentProvider.RAZORPAY,
                "RAZ" + request.getRefundId() + "REF" + UUID.randomUUID().toString()
        );
    }
}
