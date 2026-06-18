package observable;

import event.ProductEventType;
import observer.ProductObserver;

public interface ProductObservable {
    void restock(int quantity);
    void purchase(int quantity);
    void updatePrice(double newPrice);
    void subscribe(ProductEventType eventType, ProductObserver observer);
    void unsubscribe(ProductEventType eventType, ProductObserver observer);
}