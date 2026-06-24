public class StripeStatusClient implements PaymentStatusClient{

    @Override
    public PaymentStatusResult checkStatus(PaymentStatusRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment status request cannot be null.");
        }
        return new PaymentStatusResult(
                request.getPaymentId(),
                PaymentProvider.STRIPE,
                PaymentStatus.PENDING,
                PaymentProvider.STRIPE + " payment status fetched successfully"
        );
    }
}
