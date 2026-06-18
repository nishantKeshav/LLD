public class UnstablePaymentProcessor implements PaymentProcessor {

    private int attemptCount = 0;

    @Override
    public PaymentResponse process(PaymentRequest request) {
        attemptCount++;

        if (attemptCount < 3) {
            throw new RuntimeException("Temporary payment gateway failure");
        }

        System.out.println("Processing payment after retries: " + request.getPaymentId());

        return new PaymentResponse(
                request.getPaymentId(),
                true,
                "Payment processed successfully after retries"
        );
    }
}