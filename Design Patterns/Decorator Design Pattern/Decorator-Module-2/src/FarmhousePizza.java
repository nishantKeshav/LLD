public class FarmhousePizza implements Pizza {

    @Override
    public String getDescription() {
        return "Farmhouse Pizza";
    }

    @Override
    public double getCost() {
        return 220.0;
    }
}
