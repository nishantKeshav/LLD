public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMode getPaymentMode() {
        return PaymentMode.CARD;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        System.out.println("Validating card details...");
        System.out.println("Customer: " + paymentRequest.getCustomerId());
        System.out.println("Transaction ID: " + paymentRequest.getTransactionId());
        System.out.println("Paid " + paymentRequest.getAmount() + " using CARD");
        System.out.println("Remarks : " + paymentRequest.getRemarks());
    }
}
