public class VipDiscountStrategy implements DiscountStrategy {

    @Override
    public double calculateDiscount(double amount) {
        return amount * 0.30; // 30% discount for VIP customers
    }
}