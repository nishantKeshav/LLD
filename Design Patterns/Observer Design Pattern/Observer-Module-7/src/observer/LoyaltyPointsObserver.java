package observer;

import event.OrderEvent;

public class LoyaltyPointsObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "LoyaltyPointsObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("LOYALTY: Loyalty added updated for customer " + event.getCustomerId());
    }
}
