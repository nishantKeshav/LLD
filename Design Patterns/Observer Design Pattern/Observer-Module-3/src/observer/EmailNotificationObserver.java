package observer;

public class EmailNotificationObserver implements StockObserver {

    private final String userId;
    private final String email;

    public EmailNotificationObserver(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(String productName, int productQuantity) {
        System.out.println("EMAIL sent to " + email);
        System.out.println("Email Notification to User " + userId + ": Product '" + productName + "' is back in stock with quantity " + productQuantity);
    }
}
