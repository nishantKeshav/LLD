package observer;

public class SmsNotificationObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "SmsNotificationObserver";
    }

    @Override
    public void onEvent(event.OrderEvent event) {
        System.out.println("SMS: Shipping notification sent to customer " + event.getCustomerId() + " for order " + event.getOrderId());
    }
}
