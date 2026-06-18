import event.OrderEventType;
import event.OrderService;
import observable.EventDispatcher;
import observable.SimpleEventDispatcher;
import observer.*;

public class Main {
    public static void main(String[] args) {
        System.out.println("Welcome to LLD Observer Design Pattern");

        EventDispatcher dispatcher = new SimpleEventDispatcher();

        OrderEventObserver emailObserver = new EmailNotificationObserver();
        OrderEventObserver inventoryObserver = new InventoryObserver();
        OrderEventObserver invoiceObserver = new InvoiceObserver();
        OrderEventObserver loyaltyObserver = new LoyaltyPointsObserver();
        OrderEventObserver smsObserver = new SmsNotificationObserver();
        OrderEventObserver refundObserver = new RefundObserver();
        OrderEventObserver auditObserver = new AuditLogObserver();
        OrderEventObserver failingObserver = new FailingObserver();

        dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver);
        dispatcher.subscribe(OrderEventType.ORDER_PLACED, inventoryObserver);
        dispatcher.subscribe(OrderEventType.ORDER_PLACED, auditObserver);

        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, invoiceObserver);
        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, loyaltyObserver);
        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, auditObserver);
        dispatcher.subscribe(OrderEventType.PAYMENT_SUCCESS, failingObserver);

        dispatcher.subscribe(OrderEventType.ORDER_SHIPPED, smsObserver);
        dispatcher.subscribe(OrderEventType.ORDER_SHIPPED, auditObserver);

        dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, refundObserver);
        dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, inventoryObserver);
        dispatcher.subscribe(OrderEventType.ORDER_CANCELLED, auditObserver);

        // duplicate test
        dispatcher.subscribe(OrderEventType.ORDER_PLACED, emailObserver);

        OrderService orderService = new OrderService(dispatcher);

        orderService.placeOrder("ORD-1", "CUST-101", 2500.0);
        orderService.markPaymentSuccess("ORD-1", "CUST-101", 2500.0);
        orderService.shipOrder("ORD-1", "CUST-101", 2500.0);

        orderService.placeOrder("ORD-2", "CUST-202", 5000.0);
        orderService.cancelOrder("ORD-2", "CUST-202", 5000.0);

        System.out.println("Total events published: " + dispatcher.getEventHistory().size());

    }
}