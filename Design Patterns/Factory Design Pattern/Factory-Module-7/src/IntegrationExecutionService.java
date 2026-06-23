import java.util.List;
import java.util.ArrayList;

public class IntegrationExecutionService {

    private final IntegrationClientSelectionService clientSelectionService;

    public IntegrationExecutionService(IntegrationClientSelectionService clientSelectionService) {
        if (clientSelectionService == null) {
            throw new IllegalArgumentException("clientSelectionService cannot be null");
        }
        this.clientSelectionService = clientSelectionService;
    }

    public IntegrationResponse getIntegrationClient(IntegrationRequest integrationRequest) {
        IntegrationRequestValidator.validate(integrationRequest);
        List<IntegrationProvider> attemptedProviders = new ArrayList<>();
        try {
            IntegrationClient client = clientSelectionService.getIntegrationClient(integrationRequest, attemptedProviders);
            return client.execute(integrationRequest, attemptedProviders);
        } catch (IllegalArgumentException e) {
            return new IntegrationResponse(
                    integrationRequest.getRequestId(),
                    integrationRequest.getTenantId(),
                    null,
                    false,
                    "Integration execution failed",
                    null,
                    attemptedProviders,
                    e.getMessage()
            );
        }
    }

}
