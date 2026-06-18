public class FraudCheckPaymentDecorator extends PaymentProcessorDecorator{

    private final double maxAmount;

    public FraudCheckPaymentDecorator(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
        this.maxAmount = 10000.0; // Example threshold
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        if (isFraudulent(request)) {
            System.out.println("FRAUD CHECK: Suspicious payment blocked: " + request.getPaymentId());
            return new PaymentResponse(
                    request.getPaymentId(),
                    false,
                    "Payment blocked due to fraud suspicion"
            );
        }
        System.out.println("FRAUD CHECK: Payment passed fraud check for " + request.getPaymentId());
        return super.process(request);
    }

    private boolean isFraudulent(PaymentRequest request) {
        double amount = request.getAmount();
        if (amount > maxAmount) {
            System.out.println("FRAUD CHECK: Suspicious payment blocked: " + request.getPaymentId());
            return true;
        }
        return false;
    }
}
