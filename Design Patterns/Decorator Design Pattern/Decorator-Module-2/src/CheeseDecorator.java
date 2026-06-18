public class CheeseDecorator extends PizzaDecorator {

    public CheeseDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 40.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Cheese";
    }

}