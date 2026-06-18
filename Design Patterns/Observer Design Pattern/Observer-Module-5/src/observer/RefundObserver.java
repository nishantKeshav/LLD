package observer;

public class RefundObserver implements OrderEventObserver {

    @Override
    public String getObserverName() {
        return "RefundObserver";
    }

    @Override
    public void onEvent(event.OrderEvent event) {
        System.out.println("REFUND: Refund processed for order " + event.getOrderId() + " amount " + event.getAmount());
    }
}
