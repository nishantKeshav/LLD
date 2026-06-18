public class Main {

    public static void main(String[] args) {
        runNormalPaymentExample();
        System.out.println();

        runFraudPaymentExample();
        System.out.println();

        runInvalidPaymentExample();
        System.out.println();

        runRetryPaymentExample();
    }

    private static void runNormalPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-1: Normal Payment");

        PaymentMetrics metrics = new PaymentMetrics();

        PaymentProcessor processor = new BasicPaymentProcessor();
        processor = new ValidationPaymentDecorator(processor);
        processor = new FraudCheckPaymentDecorator(processor);
        processor = new AuditLogPaymentDecorator(processor);
        processor = new MetricsPaymentDecorator(processor, metrics);

        PaymentRequest request = new PaymentRequest(
                "PAY-101",
                "CUST-101",
                2500.0,
                "UPI",
                "INR"
        );

        PaymentResponse response = processor.process(request);

        printResponseAndMetrics(response, metrics);
    }

    private static void runFraudPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-2: Fraud Payment");

        PaymentMetrics metrics = new PaymentMetrics();

        PaymentProcessor processor = new BasicPaymentProcessor();
        processor = new ValidationPaymentDecorator(processor);
        processor = new FraudCheckPaymentDecorator(processor);
        processor = new AuditLogPaymentDecorator(processor);
        processor = new MetricsPaymentDecorator(processor, metrics);

        PaymentRequest fraudRequest = new PaymentRequest(
                "PAY-102",
                "CUST-999",
                150000.0,
                "CARD",
                "INR"
        );

        try {
            PaymentResponse response = processor.process(fraudRequest);
            printResponseAndMetrics(response, metrics);
        } catch (Exception e) {
            System.out.println("Final Exception: " + e.getMessage());
            printMetrics(metrics);
        }
    }

    private static void runInvalidPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-3: Invalid Payment");

        PaymentMetrics metrics = new PaymentMetrics();

        PaymentProcessor processor = new BasicPaymentProcessor();
        processor = new ValidationPaymentDecorator(processor);
        processor = new FraudCheckPaymentDecorator(processor);
        processor = new AuditLogPaymentDecorator(processor);
        processor = new MetricsPaymentDecorator(processor, metrics);

        PaymentRequest invalidRequest = new PaymentRequest(
                "PAY-103",
                "CUST-101",
                -500.0,
                "UPI",
                "INR"
        );

        try {
            PaymentResponse response = processor.process(invalidRequest);
            printResponseAndMetrics(response, metrics);
        } catch (Exception e) {
            System.out.println("Final Exception: " + e.getMessage());
            printMetrics(metrics);
        }
    }

    private static void runRetryPaymentExample() {
        System.out.println("Module 5 — Payment Processor Decorator Ex-4: Retry Payment");

        PaymentProcessor retryProcessor = new UnstablePaymentProcessor();
        retryProcessor = new RetryPaymentDecorator(retryProcessor, 2);

        PaymentRequest retryRequest = new PaymentRequest(
                "PAY-104",
                "CUST-404",
                999.0,
                "UPI",
                "INR"
        );

        PaymentResponse response = retryProcessor.process(retryRequest);

        System.out.println("Final Response: " + response.getMessage());
    }

    private static void printResponseAndMetrics(PaymentResponse response, PaymentMetrics metrics) {
        System.out.println("Final Response: " + response.getMessage());
        printMetrics(metrics);
    }

    private static void printMetrics(PaymentMetrics metrics) {
        System.out.println("Total Attempts: " + metrics.getTotalAttempts());
        System.out.println("Success Count: " + metrics.getSuccessCount());
        System.out.println("Failure Count: " + metrics.getFailureCount());
    }
}