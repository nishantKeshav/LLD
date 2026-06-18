public class WalletPaymentProcessor implements PaymentProcessor{

    @Override
    public void processPayment(PaymentRequest request) {
        System.out.println();
        System.out.println("=========================================================================");
        System.out.println("Processing Wallet payment");
        System.out.println("Payment ID: " + request.getPaymentId());
        System.out.println("Customer ID: " + request.getCustomerId());
        System.out.println("Amount: " + request.getAmount() + " " + request.getCurrency());
        System.out.println("Description: " + request.getDescription());
        System.out.println("=========================================================================");
    }
}
