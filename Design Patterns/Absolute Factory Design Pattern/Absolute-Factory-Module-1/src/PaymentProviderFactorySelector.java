public class PaymentProviderFactorySelector {

    private PaymentProviderFactorySelector() {
        // Utility class
    }

    public static PaymentProviderFactory getFactory(PaymentProvider provider) {
        if (provider == null) {
            throw new IllegalArgumentException("Payment provider cannot be null.");
        }

        return switch (provider) {
            case RAZORPAY -> new RazorpayPaymentProviderFactory();
            case STRIPE -> new StripePaymentProviderFactory();
            default -> throw new IllegalArgumentException("Payment provider not supported: " + provider);
        };
    }
}