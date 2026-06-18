public class UpiPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMode getPaymentMode() {
        return PaymentMode.UPI;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        System.out.println("Validating UPI details...");
        System.out.println("Customer: " + paymentRequest.getCustomerId());
        System.out.println("Transaction ID: " + paymentRequest.getTransactionId());
        System.out.println("Paid " + paymentRequest.getAmount() + " using UPI");
        System.out.println("Remarks : " + paymentRequest.getRemarks());
    }
}
