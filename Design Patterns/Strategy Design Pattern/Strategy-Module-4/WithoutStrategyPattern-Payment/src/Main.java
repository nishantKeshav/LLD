public class Main {
    public static void main(String[] args) {
        System.out.println("Without Strategy Pattern! - Payment");
        PaymentService paymentService = new PaymentService();

        String customerId = "1";
        double amount = 100.0;
        String paymentMode = "UPI";
        paymentService.makePayment(paymentMode, amount, customerId);

        customerId = "2";
        amount = 200.0;
        paymentMode = "CARD";
        paymentService.makePayment(paymentMode, amount, customerId);

        customerId = "3";
        amount = 300.0;
        paymentMode = "NET_BANKING";
        paymentService.makePayment(paymentMode, amount, customerId);

    }
}