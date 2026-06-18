package observer;

import event.OrderEvent;

public class InvoiceObserver implements OrderEventObserver{

    @Override
    public String getObserverName() {
        return "InvoiceObserver";
    }

    @Override
    public void onEvent(OrderEvent event) {
        System.out.println("INVOICE: Invoice generated for order " + event.getOrderId() + " amount " + event.getAmount());
    }
}
