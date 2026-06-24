public interface PaymentClient {
    PaymentResult processPayment(PaymentRequest request);
}