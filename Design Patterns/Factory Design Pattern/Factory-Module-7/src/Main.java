import java.util.List;

public class Main {

    public static void main(String[] args) {

        IntegrationClientRegistry registry = new IntegrationClientRegistry();
        IntegrationClientFactory factory = new IntegrationClientFactory(registry);
        TenantIntegrationConfigService configService = new TenantIntegrationConfigService();
        ProviderHealthService healthService = new ProviderHealthService();

        IntegrationClientSelectionService selectionService =
                new IntegrationClientSelectionService(
                        factory,
                        healthService,
                        configService
                );

        IntegrationExecutionService executionService =
                new IntegrationExecutionService(selectionService);

        System.out.println("Factory Module 7 — Configurable Integration Client Factory");

        // ==================================================
        // TEST 1:
        // TENANT-A has RAZORPAY enabled.
        // RAZORPAY supports PAYMENT + INR + INDIA.
        // Expected: RAZORPAY selected successfully.
        // ==================================================
        IntegrationRequest razorpaySuccessRequest = new IntegrationRequest(
                "REQ-101",
                "TENANT-A",
                "CUST-101",
                IntegrationOperation.PAYMENT,
                Currency.INR,
                Region.INDIA,
                1500.00,
                "IDEMP-REQ-101",
                IntegrationProvider.RAZORPAY,
                List.of(IntegrationProvider.CASHFREE)
        );

        printResponse(executionService.getIntegrationClient(razorpaySuccessRequest));


        // ==================================================
        // TEST 2:
        // TENANT-A does NOT have STRIPE enabled.
        // Fallback provider CASHFREE is enabled.
        // CASHFREE supports PAYMENT + INR + INDIA.
        // Expected: STRIPE skipped, CASHFREE selected.
        // ==================================================
        IntegrationRequest fallbackToCashfreeRequest = new IntegrationRequest(
                "REQ-102",
                "TENANT-A",
                "CUST-102",
                IntegrationOperation.PAYMENT,
                Currency.INR,
                Region.INDIA,
                2500.00,
                "IDEMP-REQ-102",
                IntegrationProvider.STRIPE,
                List.of(IntegrationProvider.CASHFREE)
        );

        printResponse(executionService.getIntegrationClient(fallbackToCashfreeRequest));


        // ==================================================
        // TEST 3:
        // TENANT-B has PAYPAL and STRIPE enabled.
        // PAYPAL is unhealthy according to ProviderHealthService.
        // STRIPE is healthy and supports PAYMENT + USD + US.
        // Expected: PAYPAL skipped, STRIPE selected.
        // ==================================================
        IntegrationRequest fallbackToStripeDueToPaypalHealthRequest = new IntegrationRequest(
                "REQ-103",
                "TENANT-B",
                "CUST-103",
                IntegrationOperation.PAYMENT,
                Currency.USD,
                Region.US,
                3000.00,
                "IDEMP-REQ-103",
                IntegrationProvider.PAYPAL,
                List.of(IntegrationProvider.STRIPE)
        );

        printResponse(executionService.getIntegrationClient(fallbackToStripeDueToPaypalHealthRequest));


        // ==================================================
        // TEST 4:
        // TENANT-B has STRIPE enabled.
        // STRIPE supports SUBSCRIPTION + USD + US.
        // Expected: STRIPE selected successfully.
        // ==================================================
        IntegrationRequest stripeSubscriptionRequest = new IntegrationRequest(
                "REQ-104",
                "TENANT-B",
                "CUST-104",
                IntegrationOperation.SUBSCRIPTION,
                Currency.USD,
                Region.US,
                999.00,
                "IDEMP-REQ-104",
                IntegrationProvider.STRIPE,
                List.of(IntegrationProvider.PAYPAL)
        );

        printResponse(executionService.getIntegrationClient(stripeSubscriptionRequest));


        // ==================================================
        // TEST 5:
        // TENANT-A has RAZORPAY and CASHFREE enabled.
        // But request is SUBSCRIPTION + EUR + EUROPE.
        //
        // RAZORPAY supports:
        // PAYMENT, REFUND, PAYOUT
        // INR
        // INDIA
        //
        // CASHFREE supports:
        // PAYMENT, PAYOUT
        // INR
        // INDIA
        //
        // So no provider can handle this request.
        // Expected: Failure response.
        // ==================================================
        IntegrationRequest noSuitableProviderRequest = new IntegrationRequest(
                "REQ-105",
                "TENANT-A",
                "CUST-105",
                IntegrationOperation.SUBSCRIPTION,
                Currency.EUR,
                Region.EUROPE,
                5000.00,
                "IDEMP-REQ-105",
                IntegrationProvider.RAZORPAY,
                List.of(IntegrationProvider.CASHFREE)
        );

        printResponse(executionService.getIntegrationClient(noSuitableProviderRequest));


        // ==================================================
        // TEST 6:
        // TENANT-B has STRIPE and PAYPAL enabled.
        // Preferred STRIPE supports USD/EUR and US/EUROPE.
        // But this request is INR + INDIA.
        // PAYPAL also does not support INR + INDIA.
        // Expected: Failure response.
        // ==================================================
        IntegrationRequest currencyRegionMismatchRequest = new IntegrationRequest(
                "REQ-106",
                "TENANT-B",
                "CUST-106",
                IntegrationOperation.PAYMENT,
                Currency.INR,
                Region.INDIA,
                1200.00,
                "IDEMP-REQ-106",
                IntegrationProvider.STRIPE,
                List.of(IntegrationProvider.PAYPAL)
        );

        printResponse(executionService.getIntegrationClient(currencyRegionMismatchRequest));
    }

    private static void printResponse(IntegrationResponse response) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Request ID: " + response.getRequestId());
        System.out.println("Tenant ID: " + response.getTenantId());
        System.out.println("Success: " + response.isSuccess());
        System.out.println("Provider Used: " + response.getProviderUsed());
        System.out.println("Message: " + response.getMessage());
        System.out.println("External Reference ID: " + response.getExternalReferenceId());
        System.out.println("Attempted Providers: " + response.getAttemptedProviders());
        System.out.println("Failure Reason: " + response.getFailureReason());
        System.out.println("==================================================");
    }
}