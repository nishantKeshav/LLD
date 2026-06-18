package observer;

import event.OrderEvent;

public class LoyaltyPointsObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "LoyaltyPointsObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("LOYALTY: Reward points added for customer " + event.getCustomerId());
    }
}