public class PremiumDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.20; // 20% discount for premium customers
    }
}