void main() {
    System.out.println("Module 1 — Coffee Add-ons (Decorator Design Pattern)");
    Coffee coffee = new SimpleCoffee();

    coffee = new MilkDecorator(coffee);
    coffee = new SugarDecorator(coffee);
    coffee = new CaramelDecorator(coffee);

    System.out.println("Description: " + coffee.getDescription());
    System.out.println("Total Cost: " + coffee.getCost());
}
