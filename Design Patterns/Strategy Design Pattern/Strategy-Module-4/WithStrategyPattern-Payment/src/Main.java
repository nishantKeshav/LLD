import java.util.List;

public class Main {
    public static void main(String[] args) {

        System.out.println("With Strategy Pattern! - Payment");
        List<PaymentStrategy> strategies = List.of(
                new UpiPaymentStrategy(),
                new CardPaymentStrategy(),
                new NetBankingPaymentStrategy()
        );

        PaymentRequest paymentRequest = new PaymentRequest(
                100.0,
                "C1",
                "TXN-8439",
                "Payment Successful"
        );

        PaymentService paymentService = new PaymentService(strategies);
        PaymentMode[] paymentModes = PaymentMode.values();
        for (PaymentMode paymentMode : paymentModes) {
            paymentService.makePayment(paymentMode, paymentRequest);
            System.out.println();
        }
    }
}