import event.OrderEvent;
import event.OrderEventType;
import event.OrderStatus;
import observable.AdvancedEventDispatcher;
import observable.DispatchResult;
import observable.ObserverRegistration;
import observable.PriorityRetryEventDispatcher;
import observer.*;

public class Main {
    public static void main(String[] args) {
        AdvancedEventDispatcher dispatcher = new PriorityRetryEventDispatcher();

        OrderEventObserver auditObserver = new AuditLogObserver();
        OrderEventObserver invoiceObserver = new InvoiceObserver();
        OrderEventObserver loyaltyObserver = new LoyaltyPointsObserver();
        OrderEventObserver highValueObserver = new HighValueOrderObserver();
        OrderEventObserver failingObserver = new FailingObserver();

        dispatcher.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(auditObserver, 1, 0, event -> true)
        );

        dispatcher.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(invoiceObserver, 2, 1, event -> true)
        );

        dispatcher.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(loyaltyObserver, 3, 1, event -> true)
        );

        dispatcher.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(highValueObserver, 4, 0, event -> event.getAmount() > 10000)
        );

        dispatcher.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(failingObserver, 5, 2, event -> true)
        );

        OrderEvent normalPaymentEvent = new OrderEvent(
                OrderEventType.PAYMENT_SUCCESS,
                "ORD-1",
                "CUST-101",
                2500.0,
                OrderStatus.PLACED,
                OrderStatus.PAID,
                "Payment successful"
        );

        DispatchResult result1 = dispatcher.publish(normalPaymentEvent);

        printDispatchResult(result1);

        OrderEvent highValuePaymentEvent = new OrderEvent(
                OrderEventType.PAYMENT_SUCCESS,
                "ORD-2",
                "CUST-202",
                15000.0,
                OrderStatus.PLACED,
                OrderStatus.PAID,
                "High value payment successful"
        );

        DispatchResult result2 = dispatcher.publish(highValuePaymentEvent);

        printDispatchResult(result2);

        System.out.println("Event history count: " + dispatcher.getEventHistory().size());
        System.out.println("Dead-letter count: " + dispatcher.getDeadLetterEvents().size());
    }

    private static void printDispatchResult(DispatchResult result) {
        System.out.println("Dispatch summary for event: " + result.getEvent().getEventType());
        System.out.println("Success: " + result.getSuccessCount());
        System.out.println("Failed: " + result.getFailedCount());
        System.out.println("Skipped: " + result.getSkippedCount());
    }
}
