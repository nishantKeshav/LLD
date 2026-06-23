import java.util.List;

public class IntegrationResponse {

    private final String requestId;
    private final String tenantId;
    private final IntegrationProvider providerUsed;
    private final boolean success;
    private final String message;
    private final String externalReferenceId;
    private final List<IntegrationProvider> attemptedProviders;
    private final String failureReason;

    public IntegrationResponse(String requestId, String tenantId, IntegrationProvider providerUsed, boolean success,
            String message, String externalReferenceId, List<IntegrationProvider> attemptedProviders, String failureReason) {
        this.requestId = requestId;
        this.tenantId = tenantId;
        this.providerUsed = providerUsed;
        this.success = success;
        this.message = message;
        this.externalReferenceId = externalReferenceId;
        this.attemptedProviders = List.copyOf(attemptedProviders);
        this.failureReason = failureReason;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getTenantId() {
        return tenantId;
    }

    public IntegrationProvider getProviderUsed() {
        return providerUsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public String getExternalReferenceId() {
        return externalReferenceId;
    }

    public List<IntegrationProvider> getAttemptedProviders() {
        return List.copyOf(attemptedProviders);
    }

    public String getFailureReason() {
        return failureReason;
    }
}