public class ExtraPaneerDecorator extends PizzaDecorator {

    protected ExtraPaneerDecorator(Pizza pizza) {
        super(pizza);
    }

    @Override
    public double getCost() {
        return pizza.getCost() + 50.0;
    }

    @Override
    public String getDescription() {
        return pizza.getDescription() + ", Extra Paneer";
    }
}
