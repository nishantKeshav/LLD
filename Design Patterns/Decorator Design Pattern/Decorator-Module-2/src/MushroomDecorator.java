public class MushroomDecorator extends PizzaDecorator {

    protected MushroomDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 35.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Mushroom";
    }
}
