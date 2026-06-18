public class NetBankingPaymentStrategy implements PaymentStrategy {

    @Override
    public PaymentMode getPaymentMode() {
        return PaymentMode.NET_BANKING;
    }

    @Override
    public void pay(PaymentRequest paymentRequest) {
        System.out.println("Validating net banking details...");
        System.out.println("Customer: " + paymentRequest.getCustomerId());
        System.out.println("Transaction ID: " + paymentRequest.getTransactionId());
        System.out.println("Paid " + paymentRequest.getAmount() + " using NET_BANKING");
        System.out.println("Remarks : " + paymentRequest.getRemarks());
    }
}
