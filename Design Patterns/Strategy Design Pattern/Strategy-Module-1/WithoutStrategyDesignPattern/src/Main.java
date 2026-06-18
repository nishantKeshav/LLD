public class Main {
    public static void main(String[] args) {
        DiscountCalculator discountCalculator = new DiscountCalculator();

        String[] customerTypes = {"REGULAR", "PREMIUM", "VIP", "NEW"};
        double[] purchaseAmounts = {1000.00, 2500.00, 5000.00, 750.00};

        for (int i = 0; i < customerTypes.length; i++) {
            String customerType = customerTypes[i];
            double amount = purchaseAmounts[i];
            double discount = discountCalculator.calculateDiscount(customerType, amount);
            double finalAmount = amount - discount;

            System.out.println("Customer Type: " + customerType);
            System.out.println("Purchase Amount: " + amount);
            System.out.println("Discount: " + discount);
            System.out.println("Final Amount: " + finalAmount);
            System.out.println();
        }
    }
}