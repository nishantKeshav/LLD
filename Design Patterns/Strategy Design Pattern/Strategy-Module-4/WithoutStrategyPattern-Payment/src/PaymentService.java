public class PaymentService {
    public void makePayment(String paymentMode, double amount, String customerId) {
        if (paymentMode.equals("UPI")) {
            System.out.println("Validating UPI for customer: " + customerId);
            System.out.println("Paid " + amount + " using UPI");
        } else if (paymentMode.equals("CARD")) {
            System.out.println("Validating card for customer: " + customerId);
            System.out.println("Paid " + amount + " using CARD");
        } else if (paymentMode.equals("NET_BANKING")) {
            System.out.println("Redirecting to bank for customer: " + customerId);
            System.out.println("Paid " + amount + " using NET BANKING");
        } else {
            System.out.println("Invalid payment mode");
        }
    }
}