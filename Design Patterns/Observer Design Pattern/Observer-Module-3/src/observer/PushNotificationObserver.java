package observer;

public class PushNotificationObserver implements StockObserver {

    private final String userId;
    private final String deviceToken;

    public PushNotificationObserver(String userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(String productName, int productQuantity) {
        System.out.println("Push notification sent to User " + userId + " with Device Token " + deviceToken);
        System.out.println("Push Notification sent to User " + userId + ": Product '" + productName + "' is back in stock with quantity " + productQuantity);
    }
}
