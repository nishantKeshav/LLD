public class IntegrationRequestValidator {

    private IntegrationRequestValidator() {
        // Utility class
    }

    public static void validate(IntegrationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Integration request cannot be null.");
        }

        if (request.getRequestId() == null || request.getRequestId().isBlank()) {
            throw new IllegalArgumentException("Request ID cannot be null or blank.");
        }

        if (request.getTenantId() == null || request.getTenantId().isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }

        if (request.getCustomerId() == null || request.getCustomerId().isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank.");
        }

        if (request.getOperation() == null) {
            throw new IllegalArgumentException("Operation cannot be null.");
        }

        if (request.getCurrency() == null) {
            throw new IllegalArgumentException("Currency cannot be null.");
        }

        if (request.getRegion() == null) {
            throw new IllegalArgumentException("Region cannot be null.");
        }

        if (request.getAmount() <= 0) {
            throw new IllegalArgumentException("Amount must be greater than zero.");
        }

        if (request.getIdempotencyKey() == null || request.getIdempotencyKey().isBlank()) {
            throw new IllegalArgumentException("Idempotency key cannot be null or blank.");
        }

        if (request.getPreferredProvider() == null) {
            throw new IllegalArgumentException("Preferred provider cannot be null.");
        }

        if (request.getFallbackProviders() == null) {
            throw new IllegalArgumentException("Fallback providers cannot be null.");
        }

        if (request.getFallbackProviders().stream().anyMatch(provider -> provider == null)) {
            throw new IllegalArgumentException("Fallback providers cannot contain null values.");
        }
    }
}
