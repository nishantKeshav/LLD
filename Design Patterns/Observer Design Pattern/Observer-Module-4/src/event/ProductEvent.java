package event;

import java.time.LocalDateTime;

public class ProductEvent {

    private final ProductEventType eventType;
    private final String productId;
    private final String productName;
    private final int availableQuantity;
    private final double oldPrice;
    private final double newPrice;
    private final String message;
    private final LocalDateTime eventTime;

    public ProductEvent(ProductEventType eventType, String productId, String productName,
                        int availableQuantity, double oldPrice, double newPrice, String message) {
        this.eventType = eventType;
        this.productId = productId;
        this.productName = productName;
        this.availableQuantity = availableQuantity;
        this.oldPrice = oldPrice;
        this.newPrice = newPrice;
        this.message = message;
        this.eventTime = LocalDateTime.now();
    }

    // Getters
    public ProductEventType getEventType() {
        return eventType;
    }
    public String getProductId() {
        return productId;
    }
    public String getProductName() {
        return productName;
    }
    public int getAvailableQuantity() {
        return availableQuantity;
    }
    public double getOldPrice() {
        return oldPrice;
    }
    public double getNewPrice() {
        return newPrice;
    }
    public String getMessage() {
        return message;
    }
    public LocalDateTime getEventTime() {
        return eventTime;
    }

    public String toString() {
        return String.format("EventType: %s, ProductId: %s, ProductName: %s, AvailableQuantity: %d, OldPrice: %.2f, NewPrice: %.2f, Message: %s, EventTime: %s",
                eventType, productId, productName, availableQuantity, oldPrice, newPrice, message, eventTime);
    }
}
