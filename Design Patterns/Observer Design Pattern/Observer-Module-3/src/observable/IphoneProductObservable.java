package observable;

import observer.StockObserver;

import java.util.List;
import java.util.ArrayList;

public class IphoneProductObservable implements ProductStockObservable {

    private final String productId;
    private final String productName;
    private int productQuantity;
    private final List<StockObserver> observers;

    public IphoneProductObservable(String productId, String productName, int productQuantity) {
        this.observers = new ArrayList<>();
        this.productId = productId;
        this.productName = productName;
        this.productQuantity = productQuantity;
    }

    @Override
    public void addObserver(StockObserver stockObserver) {
        if (observers.contains(stockObserver)) {
            System.out.println("User " + stockObserver.getUserId() + " is already subscribed for stock alert on " + productName);
            return;
        }
        this.observers.add(stockObserver);
        System.out.println("User " + stockObserver.getUserId() + " subscribed for stock alert on " + productName);
    }

    @Override
    public void removeObserver(StockObserver stockObserver) {
        if (!observers.contains(stockObserver)) {
            System.out.println("User " + stockObserver.getUserId() + " is not subscribed for stock alert on " + productName);
            return;
        }
        this.observers.remove(stockObserver);
        System.out.println("User " + stockObserver.getUserId() + " unsubscribed from stock alert on " + productName);
    }

    @Override
    public boolean purchase(int quantity) {
        if (quantity > productQuantity) {
            System.out.println("Purchase failed: " + productName + " is out of stock");
            System.out.println("Available quantity: " + productQuantity);
            return false;
        }
        productQuantity -= quantity;
        System.out.println("Purchase successful: " + quantity + " " + productName + "(s) purchased");
        System.out.println("Remaining stock: " + productQuantity);
        return true;
    }

    @Override
    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero");
        }
        int prevProductQuantity = productQuantity;
        productQuantity += quantity;
        System.out.println("Restocked " + productName + " with quantity: " + quantity);
        System.out.println("Current stock: " + productQuantity);
        if (prevProductQuantity == 0 && productQuantity > 0) {
            notifyObservers();
        }
    }

    private void notifyObservers() {
        System.out.println("Notifying observers about restock of " + productName);
        List<StockObserver> observersToNotify = new ArrayList<>(observers);
        for (StockObserver observer : observersToNotify) {
            observer.update(productName, productQuantity);
        }
        System.out.println("All observers notified about restock of " + productName);
    }
}
