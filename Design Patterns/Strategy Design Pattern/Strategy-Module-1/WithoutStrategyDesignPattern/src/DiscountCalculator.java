public class DiscountCalculator {

    public double calculateDiscount(String customerType, double amount) {
        if (customerType.equals("REGULAR")) {
            return amount * 0.10;
        } else if (customerType.equals("PREMIUM")) {
            return amount * 0.20;
        } else if (customerType.equals("VIP")) {
            return amount * 0.30;
        }
        return 0;
    }
}