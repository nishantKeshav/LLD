void main() {
    System.out.println("Module 2: Pizza Toppings Decorator (Decorator Design Pattern)");

    Pizza pizza = new MargheritaPizza();

    pizza = new CheeseDecorator(pizza);
    pizza = new OlivesDecorator(pizza);
    pizza = new MushroomDecorator(pizza);

    System.out.println("Description: " + pizza.getDescription());
    System.out.println("Total Cost: " + pizza.getCost());
}
