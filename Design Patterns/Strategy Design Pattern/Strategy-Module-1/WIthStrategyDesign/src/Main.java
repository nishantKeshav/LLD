public class Main {
    public static void main(String[] args) {
        System.out.println("With Strategy Pattern");
        DiscountCalculator discountCalculator;

        Double amount = 100.30;
        System.out.println("Customer 1");
        RegularDiscountStrategy regularDiscountStrategy = new RegularDiscountStrategy();
        discountCalculator = new DiscountCalculator(regularDiscountStrategy);
        double discount = discountCalculator.calculateDiscount(amount);
        double finalAmount = amount - discount;
        System.out.println("Purchase Amount: " + amount);
        System.out.println("Discount: " + discount);
        System.out.println("Final Amount: " + finalAmount);


        System.out.println("Customer 2");
        PremiumDiscountStrategy premiumDiscountStrategy = new PremiumDiscountStrategy();
        discountCalculator = new DiscountCalculator(premiumDiscountStrategy);
        discount = discountCalculator.calculateDiscount(amount);
        finalAmount = amount - discount;
        System.out.println("Purchase Amount: " + amount);
        System.out.println("Discount: " + discount);
        System.out.println("Final Amount: " + finalAmount);


        System.out.println("Customer 3");
        VipDiscountStrategy vipDiscountStrategy = new VipDiscountStrategy();
        discountCalculator = new DiscountCalculator(vipDiscountStrategy);
        discount = discountCalculator.calculateDiscount(amount);
        finalAmount = amount - discount;
        System.out.println("Purchase Amount: " + amount);
        System.out.println("Discount: " + discount);
        System.out.println("Final Amount: " + finalAmount);

    }
}