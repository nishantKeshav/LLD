public class PaymentProcessorFactory {

    private PaymentProcessorFactory() {
        throw new IllegalStateException("Utility class");
    }

    public static PaymentProcessor getProcessor(PaymentMode paymentMode) {
        if (paymentMode == null) {
            throw new IllegalStateException("PaymentMode is null");
        }
        return switch (paymentMode) {
            case UPI -> new UpiPaymentProcessor();
            case CARD -> new CardPaymentProcessor();
            case WALLET -> new WalletPaymentProcessor();
            case NET_BANKING -> new NetBankingPaymentProcessor();
            default -> throw new IllegalStateException("Unknown paymentMode");
        };
    }
}
