public class PaymentResult {

    private final String paymentId;
    private final PaymentProvider provider;
    private final boolean success;
    private final String message;
    private final String providerReferenceId;

    public PaymentResult(String paymentId, PaymentProvider provider, boolean success, String message, String providerReferenceId) {
        if (paymentId == null || paymentId.isBlank()) {
            throw new IllegalArgumentException("Payment ID cannot be null or blank.");
        }

        if (provider == null) {
            throw new IllegalArgumentException("Provider cannot be null.");
        }

        if (message == null || message.isBlank()) {
            throw new IllegalArgumentException("Message cannot be null or blank.");
        }

        if (success && (providerReferenceId == null || providerReferenceId.isBlank())) {
            throw new IllegalArgumentException("Provider reference ID is required for successful payment.");
        }
        this.paymentId = paymentId;
        this.provider = provider;
        this.success = success;
        this.message = message;
        this.providerReferenceId = providerReferenceId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getProviderReferenceId() {
        return providerReferenceId;
    }

}
