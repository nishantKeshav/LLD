public class MetricsPaymentDecorator extends PaymentProcessorDecorator{

    private final PaymentMetrics metrics;

    public MetricsPaymentDecorator(PaymentProcessor paymentProcessor, PaymentMetrics paymentMetrics) {
        super(paymentProcessor);
        this.metrics = paymentMetrics;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        metrics.incrementTotalAttempts();
        PaymentResponse response = super.process(request);

        if (response.isSuccess()) {
            metrics.incrementSuccessCount();
        } else {
            metrics.incrementFailureCount();
        }

        System.out.println("METRICS: Payment metrics updated for " + request.getPaymentId());

        return response;
    }
}
