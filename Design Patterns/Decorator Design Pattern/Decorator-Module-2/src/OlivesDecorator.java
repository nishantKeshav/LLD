public class OlivesDecorator extends PizzaDecorator {

    protected OlivesDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 30.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Olives";
    }
}
