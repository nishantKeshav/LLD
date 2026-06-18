package observer;

import event.ProductEvent;

public interface ProductObserver {
    String getUserId();
    void update(ProductEvent event);
}