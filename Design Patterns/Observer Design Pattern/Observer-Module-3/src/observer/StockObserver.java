package observer;

public interface StockObserver {
    String getUserId();
    void update(String productName, int productQuantity);
}
