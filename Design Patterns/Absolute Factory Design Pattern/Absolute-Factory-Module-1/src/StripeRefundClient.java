import java.util.UUID;

public class StripeRefundClient implements RefundClient{

    @Override
    public RefundResult processRefund(RefundRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }
        System.out.println("Refund processed through " + PaymentProvider.STRIPE);
        return new RefundResult(
                request.getRefundId(),
                request.getPaymentId(),
                PaymentProvider.STRIPE,
                true,
                "Refund Successful through " + PaymentProvider.STRIPE,
                "STR" + request.getRefundId() + "REF" + UUID.randomUUID().toString()
        );
    }
}
