public class ValidationPaymentDecorator extends PaymentProcessorDecorator{

    public ValidationPaymentDecorator(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        validatePaymentRequest(request);
        System.out.println("VALIDATION: Payment request validated for " + request.getPaymentId());
        return super.process(request);
    }

    private void validatePaymentRequest(PaymentRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Payment request cannot be null.");
        }
        String paymentId = request.getPaymentId();
        if (paymentId == null || paymentId.isEmpty()) {
            System.out.println("Validation failed: Payment ID is required.");
            throw new IllegalArgumentException("Payment ID is required.");
        }
        String customerId = request.getCustomerId();
        if (customerId == null || customerId.isEmpty()) {
            System.out.println("Validation failed: Customer ID is required.");
            throw new IllegalArgumentException("Customer ID is required.");
        }
        double amount = request.getAmount();
        if (amount <= 0) {
            System.out.println("Validation failed: Amount must be greater than zero.");
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }
        String currency = request.getCurrency();
        if (currency == null || currency.isEmpty()) {
            System.out.println("Validation failed: Currency is required.");
            throw new IllegalArgumentException("Currency is required.");
        }
        String paymentMode = request.getPaymentMode();
        if (paymentMode == null || paymentMode.isEmpty()) {
            System.out.println("Validation failed: Payment mode is required.");
            throw new IllegalArgumentException("Payment mode is required.");
        }
    }
}
