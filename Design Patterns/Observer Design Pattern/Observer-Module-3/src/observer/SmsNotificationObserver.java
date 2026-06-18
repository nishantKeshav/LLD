package observer;

public class SmsNotificationObserver implements StockObserver {

    private final String userId;
    private final String phoneNumber;

    public SmsNotificationObserver(String userId, String phoneNumber) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(String productName, int productQuantity) {
        System.out.println("SMS sent to " + phoneNumber);
        System.out.println("SMS Notification to User " + userId + ": Product '" + productName + "' is back in stock with quantity " + productQuantity);
    }
}
