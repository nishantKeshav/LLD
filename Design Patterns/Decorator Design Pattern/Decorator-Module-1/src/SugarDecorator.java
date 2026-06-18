public class SugarDecorator extends CoffeeDecorator {

    public SugarDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return this.coffee.getDescription() + " ,Sugar";
    }

    @Override
    public double getCost() {
        return this.coffee.getCost() + 5.0;
    }
}
