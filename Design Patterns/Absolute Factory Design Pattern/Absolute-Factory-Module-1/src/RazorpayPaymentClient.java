public class RazorpayPaymentClient implements PaymentClient{

    @Override
    public PaymentResult processPayment(PaymentRequest request) {
        if (request == null) {
            throw new NullPointerException("Request cannot be null");
        }
        System.out.println("Payment Requested through " + PaymentProvider.RAZORPAY);
        return new PaymentResult(
                request.getPaymentId(),
                PaymentProvider.RAZORPAY,
                true,
                "Payment Successful through " + PaymentProvider.RAZORPAY,
                "RAZ" + request.getPaymentId() + "-" + request.getIdempotencyKey()
        );
    }
}
