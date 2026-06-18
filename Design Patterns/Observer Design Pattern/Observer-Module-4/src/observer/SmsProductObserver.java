package observer;

public class SmsProductObserver implements ProductObserver {

    private final String userId;
    private final String phoneNumber;

    public SmsProductObserver(String userId, String phoneNumber) {
        this.userId = userId;
        this.phoneNumber = phoneNumber;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(event.ProductEvent event) {
        System.out.println("======================================================================");
        System.out.println("SMS sent to " + phoneNumber);
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
