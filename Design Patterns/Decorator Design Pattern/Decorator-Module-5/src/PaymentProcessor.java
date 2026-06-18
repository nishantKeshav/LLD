public interface PaymentProcessor {
    PaymentResponse process(PaymentRequest request);
}