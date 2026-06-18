public class RegularDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.10; // 10% discount for regular customers
    }
}