public class WebhookPayload {

    private final PaymentProvider provider;
    private final String payload;
    private final String signature;
    private final String eventType;

    public WebhookPayload(PaymentProvider provider, String payload, String signature, String eventType) {
        if (provider == null) {
            throw new IllegalArgumentException("provider cannot be null");
        }
        if (payload == null || payload.isBlank()) {
            throw new IllegalArgumentException("payload cannot be null or blank");
        }
        if (signature == null || signature.isBlank()) {
            throw new IllegalArgumentException("signature cannot be null or blank");
        }
        if (eventType == null || eventType.isBlank()) {
            throw new IllegalArgumentException("eventType cannot be null or blank");
        }
        this.provider = provider;
        this.payload = payload;
        this.signature = signature;
        this.eventType = eventType;
    }

    public PaymentProvider getProvider() {
        return provider;
    }

    public String getPayload() {
        return payload;
    }

    public String getSignature() {
        return signature;
    }

    public String getEventType() {
        return eventType;
    }

}
