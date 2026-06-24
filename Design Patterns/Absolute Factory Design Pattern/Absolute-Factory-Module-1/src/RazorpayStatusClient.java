public class RazorpayStatusClient implements PaymentStatusClient {

    @Override
    public PaymentStatusResult checkStatus(PaymentStatusRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment status request cannot be null.");
        }
        return new PaymentStatusResult(
                request.getPaymentId(),
                PaymentProvider.RAZORPAY,
                PaymentStatus.PENDING,
                PaymentProvider.RAZORPAY + " payment status fetched successfully"
        );
    }
}