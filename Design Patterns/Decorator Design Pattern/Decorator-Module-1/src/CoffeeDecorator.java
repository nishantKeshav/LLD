public abstract class CoffeeDecorator implements Coffee {

    public final Coffee coffee;

    protected CoffeeDecorator(Coffee coffee) {
        this.coffee = coffee;
    }

    @Override
    public String getDescription() {
        return this.coffee.getDescription();
    }

    @Override
    public double getCost() {
        return this.coffee.getCost();
    }

}