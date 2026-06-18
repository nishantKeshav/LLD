public class MilkDecorator extends CoffeeDecorator {

    public MilkDecorator(Coffee coffee) {
        super(coffee);
    }

    @Override
    public String getDescription() {
        return this.coffee.getDescription() + " ,Milk";
    }

    @Override
    public double getCost() {
        return this.coffee.getCost() + 10.0;
    }

}