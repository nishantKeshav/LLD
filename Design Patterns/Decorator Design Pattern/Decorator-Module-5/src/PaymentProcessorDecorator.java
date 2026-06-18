public abstract class PaymentProcessorDecorator implements PaymentProcessor {

    private final PaymentProcessor paymentProcessor;

    protected PaymentProcessorDecorator(PaymentProcessor paymentProcessor) {
        if (paymentProcessor == null) {
            throw new IllegalArgumentException("PaymentProcessor cannot be null.");
        }
        this.paymentProcessor = paymentProcessor;
    }

    public PaymentProcessor getWrappedProcessor() {
        return paymentProcessor;
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        return paymentProcessor.process(request);
    }

}
