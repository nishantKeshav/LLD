package observer;

import event.ProductEvent;

public class EmailProductObserver implements ProductObserver {

    private final String userId;
    private final String email;

    public EmailProductObserver(String userId, String email) {
        this.userId = userId;
        this.email = email;
    }

    @Override
    public String getUserId() {
        return userId;
    }

    @Override
    public void update(ProductEvent event) {
        System.out.println("======================================================================");
        System.out.println("EMAIL sent to " + email);
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
