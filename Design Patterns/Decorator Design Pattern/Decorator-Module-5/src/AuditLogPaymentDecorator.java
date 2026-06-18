public class AuditLogPaymentDecorator extends PaymentProcessorDecorator{

    public AuditLogPaymentDecorator(PaymentProcessor paymentProcessor) {
        super(paymentProcessor);
    }

    @Override
    public PaymentResponse process(PaymentRequest request) {
        System.out.println("AUDIT: Starting payment " + request.getPaymentId());
        PaymentResponse response = super.process(request);
        System.out.println("AUDIT: Finished payment "
                + request.getPaymentId()
                + " with status "
                + (response.isSuccess() ? "SUCCESS" : "FAILED"));
        return response;
    }

}
