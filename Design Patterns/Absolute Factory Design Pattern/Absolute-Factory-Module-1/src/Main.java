public class Main {

    public static void main(String[] args) {

        PaymentGatewayService paymentGatewayService = new PaymentGatewayService();

        System.out.println("Abstract Factory Module 1 — Payment Provider Toolkit");

        // ==================================================
        // RAZORPAY TEST DATA
        // ==================================================

        PaymentRequest razorpayPaymentRequest = new PaymentRequest(
                "PAY-101",
                "CUST-101",
                1500.00,
                "INR",
                "Mobile recharge payment",
                "IDEMP-PAY-101"
        );

        RefundRequest razorpayRefundRequest = new RefundRequest(
                "REF-101",
                "PAY-101",
                500.00,
                "Customer requested partial refund"
        );

        PaymentStatusRequest razorpayStatusRequest = new PaymentStatusRequest(
                "PAY-101"
        );

        WebhookPayload razorpayWebhookPayload = new WebhookPayload(
                PaymentProvider.RAZORPAY,
                "{ \"paymentId\": \"PAY-101\", \"status\": \"captured\" }",
                "razorpay-signature-123",
                "payment.captured"
        );

        // ==================================================
        // STRIPE TEST DATA
        // ==================================================

        PaymentRequest stripePaymentRequest = new PaymentRequest(
                "PAY-102",
                "CUST-102",
                2500.00,
                "USD",
                "Subscription payment",
                "IDEMP-PAY-102"
        );

        RefundRequest stripeRefundRequest = new RefundRequest(
                "REF-102",
                "PAY-102",
                1000.00,
                "Subscription cancelled"
        );

        PaymentStatusRequest stripeStatusRequest = new PaymentStatusRequest(
                "PAY-102"
        );

        WebhookPayload stripeWebhookPayload = new WebhookPayload(
                PaymentProvider.STRIPE,
                "{ \"paymentId\": \"PAY-102\", \"status\": \"succeeded\" }",
                "stripe-signature-456",
                "payment_intent.succeeded"
        );

        // ==================================================
        // RAZORPAY FAMILY EXECUTION
        // ==================================================

        PaymentResult razorpayPaymentResult =
                paymentGatewayService.processPayment(
                        PaymentProvider.RAZORPAY,
                        razorpayPaymentRequest
                );

        printPaymentResult(razorpayPaymentResult);

        RefundResult razorpayRefundResult =
                paymentGatewayService.processRefund(
                        PaymentProvider.RAZORPAY,
                        razorpayRefundRequest
                );

        printRefundResult(razorpayRefundResult);

        PaymentStatusResult razorpayStatusResult =
                paymentGatewayService.checkStatus(
                        PaymentProvider.RAZORPAY,
                        razorpayStatusRequest
                );

        printPaymentStatusResult(razorpayStatusResult);

        WebhookVerificationResult razorpayWebhookResult =
                paymentGatewayService.verifyWebhook(
                        PaymentProvider.RAZORPAY,
                        razorpayWebhookPayload
                );

        printWebhookVerificationResult(razorpayWebhookResult);

        // ==================================================
        // STRIPE FAMILY EXECUTION
        // ==================================================

        PaymentResult stripePaymentResult =
                paymentGatewayService.processPayment(
                        PaymentProvider.STRIPE,
                        stripePaymentRequest
                );

        printPaymentResult(stripePaymentResult);

        RefundResult stripeRefundResult =
                paymentGatewayService.processRefund(
                        PaymentProvider.STRIPE,
                        stripeRefundRequest
                );

        printRefundResult(stripeRefundResult);

        PaymentStatusResult stripeStatusResult =
                paymentGatewayService.checkStatus(
                        PaymentProvider.STRIPE,
                        stripeStatusRequest
                );

        printPaymentStatusResult(stripeStatusResult);

        WebhookVerificationResult stripeWebhookResult =
                paymentGatewayService.verifyWebhook(
                        PaymentProvider.STRIPE,
                        stripeWebhookPayload
                );

        printWebhookVerificationResult(stripeWebhookResult);
    }

    private static void printPaymentResult(PaymentResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: PAYMENT");
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Provider Reference ID: " + result.getProviderReferenceId());
        System.out.println("==================================================");
    }

    private static void printRefundResult(RefundResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: REFUND");
        System.out.println("Refund ID: " + result.getRefundId());
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Success: " + result.isSuccess());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Provider Refund Reference ID: " + result.getProviderRefundReferenceId());
        System.out.println("==================================================");
    }

    private static void printPaymentStatusResult(PaymentStatusResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: STATUS");
        System.out.println("Payment ID: " + result.getPaymentId());
        System.out.println("Status: " + result.getStatus());
        System.out.println("Message: " + result.getMessage());
        System.out.println("==================================================");
    }

    private static void printWebhookVerificationResult(WebhookVerificationResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Provider: " + result.getProvider());
        System.out.println("Operation: WEBHOOK_VERIFICATION");
        System.out.println("Event Type: " + result.getEventType());
        System.out.println("Valid: " + result.isValid());
        System.out.println("Message: " + result.getMessage());
        System.out.println("==================================================");
    }
}