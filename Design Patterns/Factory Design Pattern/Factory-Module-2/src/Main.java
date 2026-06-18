public class Main {
    public static void main() {
        System.out.println("Factory Module 2 — Payment Processor Factory");
        PaymentRequest upiRequest = new PaymentRequest(
                "PAY-101",
                "CUST-101",
                500.0,
                "INR",
                PaymentMode.UPI,
                "Mobile recharge payment"
        );
        PaymentProcessor upiProcessor = PaymentProcessorFactory.getProcessor(upiRequest.getPaymentMode());
        upiProcessor.processPayment(upiRequest);

        PaymentRequest cardRequest = new PaymentRequest(
                "PAY-102",
                "CUST-102",
                2500.0,
                "INR",
                PaymentMode.CARD,
                "Online shopping payment"
        );
        PaymentProcessor cardProcessor = PaymentProcessorFactory.getProcessor(cardRequest.getPaymentMode());
        cardProcessor.processPayment(cardRequest);

        PaymentRequest netBankingRequest = new PaymentRequest(
                "PAY-103",
                "CUST-103",
                10000.0,
                "INR",
                PaymentMode.NET_BANKING,
                "Tuition fee payment"
        );
        PaymentProcessor netBankingProcessor = PaymentProcessorFactory.getProcessor(netBankingRequest.getPaymentMode());
        netBankingProcessor.processPayment(netBankingRequest);

        PaymentRequest walletRequest = new PaymentRequest(
                "PAY-104",
                "CUST-104",
                999.0,
                "INR",
                PaymentMode.WALLET,
                "Food delivery payment"
        );
        PaymentProcessor walletProcessor = PaymentProcessorFactory.getProcessor(walletRequest.getPaymentMode());
        walletProcessor.processPayment(walletRequest);
    }
}