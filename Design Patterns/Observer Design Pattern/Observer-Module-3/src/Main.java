import observer.StockObserver;

import observer.SmsNotificationObserver;
import observable.ProductStockObservable;
import observer.PushNotificationObserver;
import observer.EmailNotificationObserver;
import observable.IphoneProductObservable;

public class Main {
    public static void main(String[] args) {

        System.out.println("Observer Design Pattern - E-Commerce Notify Me");
        ProductStockObservable product = new IphoneProductObservable("P-101", "iPhone 15", 2);

        StockObserver user1 = new EmailNotificationObserver("CUST-101", "amit@gmail.com");
        StockObserver user2 = new SmsNotificationObserver("CUST-102", "9876543210");
        StockObserver user3 = new PushNotificationObserver("CUST-103", "device-token-123");

        product.purchase(1);
        product.purchase(1);

        boolean success = product.purchase(1);

        if (!success) {
            product.addObserver(user1);
            product.addObserver(user2);
            product.addObserver(user3);
        }

        product.addObserver(user1); // duplicate test

        product.restock(5);

        product.removeObserver(user2);

        product.purchase(5);

        product.restock(3);

    }
}