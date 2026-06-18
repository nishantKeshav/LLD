import event.ProductEventType;

import observable.Product;
import observer.ProductObserver;
import observer.SmsProductObserver;
import observer.PushProductObserver;
import observable.ProductObservable;
import observer.EmailProductObserver;

public class Main {
    public static void main(String[] args) {
        System.out.println("Observer Module 4: Multi-Event Product Alert System");

        ProductObservable product = new Product(
                80000.0,
                2,
                "P-101",
                "IPhone 15",
                1
        );

        ProductObserver user1 = new EmailProductObserver("CUST-101", "amit@gmail.com");
        ProductObserver user2 = new SmsProductObserver("CUST-102", "9876543210");
        ProductObserver user3 = new PushProductObserver("CUST-103", "device-token-123");

        product.subscribe(ProductEventType.RESTOCK, user1);
        product.subscribe(ProductEventType.PRICE_DROP, user2);
        product.subscribe(ProductEventType.RESTOCK, user3);
        product.subscribe(ProductEventType.PRICE_DROP, user3);
        product.subscribe(ProductEventType.LOW_STOCK, user1);

        product.subscribe(ProductEventType.RESTOCK, user1); // duplicate test

        product.purchase(1);   // stock 2 -> 1, should trigger LOW_STOCK
        product.purchase(1);   // stock 1 -> 0
        product.purchase(1);   // fail

        product.restock(5);    // should trigger RESTOCK

        product.updatePrice(85000.0); // no notification, price increased
        product.updatePrice(75000.0); // should trigger PRICE_DROP

        product.unsubscribe(ProductEventType.PRICE_DROP, user2);

        product.updatePrice(70000.0); // only user3 gets PRICE_DROP
    }
}