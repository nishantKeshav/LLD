package observer;

public class PushProductObserver implements ProductObserver {

    private final String userId;
    private final String deviceToken;

    public PushProductObserver(String userId, String deviceToken) {
        this.userId = userId;
        this.deviceToken = deviceToken;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(event.ProductEvent event) {
        System.out.println("======================================================================");
        System.out.println("Push Notification sent to device token: " + deviceToken);
        System.out.println("User: " + userId);
        System.out.println("Event: " + event.getEventType());
        System.out.println("Product: " + event.getProductName());
        System.out.println("Message: " + event.getMessage());
        System.out.println("Time: " + event.getEventTime());
        System.out.println("======================================================================");
        System.out.println();
        System.out.println();
    }
}
