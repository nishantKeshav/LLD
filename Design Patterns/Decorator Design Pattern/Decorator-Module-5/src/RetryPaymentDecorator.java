public class RetryPaymentDecorator extends PaymentProcessorDecorator {

    private final int maxAttempts;

    public RetryPaymentDecorator(PaymentProcessor paymentProcessor, int maxAttempts) {
        super(paymentProcessor);
        this.maxAttempts = maxAttempts;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        int attempts = 0;
        int totalAttempts = maxAttempts + 1;
        String failedMessage = null;

        while (attempts < totalAttempts) {
            attempts++;

            try {
                System.out.println("RETRY: Attempt " + attempts + " for payment " + request.getPaymentId());
                return super.process(request);
            } catch (Exception e) {
                failedMessage = e.getMessage();
                System.out.println("RETRY: Attempt " + attempts + " failed: " + failedMessage);
            }
        }

        return new PaymentResponse(
                request.getPaymentId(),
                false,
                "Payment failed after retries: " + failedMessage
        );
    }

}
