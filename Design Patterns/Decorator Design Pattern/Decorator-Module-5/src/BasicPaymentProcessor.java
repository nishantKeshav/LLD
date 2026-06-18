public class BasicPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResponse process(PaymentRequest request) {
        System.out.println("Processing payment: " + request.getPaymentId());
        return new PaymentResponse(
                request.getPaymentId(),
                true,
                "Payment processed successfully"
        );
    }
}
