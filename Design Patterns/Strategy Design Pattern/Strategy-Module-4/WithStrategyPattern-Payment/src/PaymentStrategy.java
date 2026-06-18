public interface PaymentStrategy {
    PaymentMode getPaymentMode();
    void pay(PaymentRequest paymentRequest);
}
