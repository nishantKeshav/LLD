package observable;

import observer.StockObserver;

public interface ProductStockObservable {
    void addObserver(StockObserver stockObserver);
    void removeObserver(StockObserver stockObserver);
    boolean purchase(int quantity);
    void restock(int quantity);
}
