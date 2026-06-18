package observable;

import java.util.Map;
import java.util.List;
import java.util.EnumMap;
import java.util.ArrayList;

import event.ProductEvent;
import event.ProductEventType;
import observer.ProductObserver;

public class Product implements ProductObservable {

    private double price;
    private int stockQuantity;
    private final String productId;
    private final String productName;
    private final int lowStockThreshold;
    private final Map<ProductEventType, List<ProductObserver>> observersByEventType;

    public Product(double price, int stockQuantity, String productId, String productName,
                   int lowStockThreshold) {
        this.price = price;
        this.productId = productId;
        this.productName = productName;
        this.stockQuantity = stockQuantity;
        this.lowStockThreshold = lowStockThreshold;
        this.observersByEventType = new EnumMap<>(ProductEventType.class);
    }

    @Override
    public void subscribe(ProductEventType eventType, ProductObserver observer) {
        // 1. if the event type is not in the map
        if (!observersByEventType.containsKey(eventType)) {
            System.out.println("No observer registered for " + eventType);
            List<ProductObserver> observers = new ArrayList<>();
            observers.add(observer);
            observersByEventType.put(eventType, observers);
            System.out.println(observer.getUserId() + " subscribed for " + eventType + " alerts on " + productName);
            return;
        }
        // 2. if the event type is in the map
        List<ProductObserver> observers = observersByEventType.get(eventType);
        // 2.1 if the observer is already subscribed
        if (observers.contains(observer)) {
            System.out.println(observer.getUserId() + " is already subscribed for " + eventType + " alerts on " + productName);
            return;
        }
        // 2.2 if the observer is not subscribed
        observers.add(observer);
        observersByEventType.put(eventType, observers);
        System.out.println(observer.getUserId() + " subscribed for " + eventType + " alerts on " + productName);
    }

    @Override
    public void unsubscribe(ProductEventType eventType, ProductObserver observer) {
        if (!observersByEventType.containsKey(eventType)) {
            System.out.println("No observer registered for " + eventType);
            return;
        }
        List<ProductObserver> observers = observersByEventType.get(eventType);
        if (!observers.contains(observer)) {
            System.out.println(observer.getUserId() + " is not subscribed for " + eventType + " alerts on " + productName);
            return;
        }
        observers.remove(observer);
        observersByEventType.put(eventType, observers);
        System.out.println(observer.getUserId() + " unsubscribed from " + eventType + " alerts on " + productName);
    }


    @Override
    public void purchase(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Purchase quantity must be greater than zero");
        }
        if (quantity > stockQuantity) {
            System.out.println("Buying " + quantity + " for product " + productName + " exceeds product Quantity " + stockQuantity);
            return;
        }
        int prevStockQuantity = stockQuantity;
        stockQuantity -= quantity;
        System.out.println("Purchase successful: " + quantity + " units of " + productName);
        System.out.println("Remaining stock: " + stockQuantity);
        if (stockQuantity <= lowStockThreshold && prevStockQuantity > lowStockThreshold) {
            System.out.println("Product " + productName + " LOW STOCK ALERT triggered");
            ProductEvent event = new ProductEvent(
                    ProductEventType.LOW_STOCK,
                    productId,
                    productName,
                    stockQuantity,
                    price,
                    price,
                    "Only " + stockQuantity + " units left in stock"
            );
            notifyObservers(ProductEventType.LOW_STOCK, event);
        }
    }

    @Override
    public void restock(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Restock quantity must be greater than zero");
        }
        int prevStockQuantity = stockQuantity;
        stockQuantity += quantity;
        System.out.println("Restock successful: " + quantity + " units of " + productName);
        System.out.println("Current stock: " + stockQuantity);
        if (prevStockQuantity <= 0 && stockQuantity > 0) {
            System.out.println("Product " + productName + " BACK IN STOCK ALERT triggered");
            ProductEvent event = new ProductEvent(
                    ProductEventType.RESTOCK,
                    productId,
                    productName,
                    stockQuantity,
                    price,
                    price,
                    productName + " is back in stock"
            );
            notifyObservers(ProductEventType.RESTOCK, event);
        }
    }

    @Override
    public void updatePrice(double newPrice) {
        double oldPrice = price;
        if (newPrice >= price) {
            price = newPrice;
            System.out.println("Price update successful: " + productName + " new price is " + price);
            return;
        }
        price = newPrice;
        System.out.println("Price update successful: " + productName + " new price is " + price);
        System.out.println("Price update alert triggered for " + productName);
        System.out.println("Old price: " + oldPrice);
        System.out.println("New price: " + newPrice);
        System.out.println("PRICE DROP ALERT triggered");
        ProductEvent event = new ProductEvent(
                ProductEventType.PRICE_DROP,
                productId,
                productName,
                stockQuantity,
                oldPrice,
                newPrice,
                "Price dropped from " + oldPrice + " to " + newPrice
        );
        notifyObservers(ProductEventType.PRICE_DROP, event);
    }

    private void notifyObservers(ProductEventType eventType, ProductEvent event) {
        List<ProductObserver> observers = observersByEventType.get(eventType);
        if (observers == null || observers.isEmpty()) {
            return;
        }
        List<ProductObserver> observersToNotify = new ArrayList<>(observers);
        for (ProductObserver observer : observersToNotify) {
            observer.update(event);
        }
    }
}
