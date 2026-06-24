public class WebhookVerificationResult {

    private final PaymentProvider provider;
    private final boolean valid;
    private final String message;
    private final String eventType;

    public WebhookVerificationResult(PaymentProvider provider, boolean valid, String message, String eventType) {
        this.provider = provider;
        this.valid = valid;
        this.message = message;
        this.eventType = eventType;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public String getEventType() {
        return eventType;
    }
}
