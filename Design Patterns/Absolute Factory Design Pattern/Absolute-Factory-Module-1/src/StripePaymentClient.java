public class StripePaymentClient implements PaymentClient{

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }
        System.out.println("Payment Requested through " + PaymentProvider.STRIPE);
        return new PaymentResult(
                request.getPaymentId(),
                PaymentProvider.STRIPE,
                true,
                "Payment Successful through " + PaymentProvider.STRIPE,
                "STR" + request.getPaymentId() + "-" + request.getIdempotencyKey()
        );
    }
}
