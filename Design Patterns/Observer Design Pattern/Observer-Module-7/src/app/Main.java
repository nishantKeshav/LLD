package app;

import bus.InMemoryQueuedEventBus;
import bus.QueuedEventBus;
import event.DeadLetterEvent;
import event.OrderEvent;
import event.OrderEventType;
import event.OrderStatus;
import metrics.EventBusMetrics;
import observer.AuditLogObserver;
import observer.FailingObserver;
import observer.HighValueOrderObserver;
import observer.InvoiceObserver;
import observer.LoyaltyPointsObserver;
import observer.ObserverRegistration;
import observer.OrderEventObserver;
import result.DispatchResult;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        QueuedEventBus eventBus = new InMemoryQueuedEventBus();

        OrderEventObserver auditObserver = new AuditLogObserver();
        OrderEventObserver invoiceObserver = new InvoiceObserver();
        OrderEventObserver loyaltyObserver = new LoyaltyPointsObserver();
        OrderEventObserver highValueObserver = new HighValueOrderObserver();
        OrderEventObserver failingObserver = new FailingObserver();

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(auditObserver, 1, 0, event -> true)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(invoiceObserver, 2, 1, event -> true)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(loyaltyObserver, 3, 1, event -> true)
        );

        eventBus.subscribe(
                OrderEventType.PAYMENT_SUCCESS,
                new ObserverRegistration(highValueObserver, 4, 0, event -> event.getAmount() > 10000)
        );

        eventBus.subscribe(
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

        OrderEvent highValuePaymentEvent = new OrderEvent(
                OrderEventType.PAYMENT_SUCCESS,
                "ORD-2",
                "CUST-202",
                15000.0,
                OrderStatus.PLACED,
                OrderStatus.PAID,
                "High value payment successful"
        );

        eventBus.publish(normalPaymentEvent);
        eventBus.publish(highValuePaymentEvent);

        System.out.println("Pending events before processing: " + eventBus.getPendingEvents().size());

        List<DispatchResult> results = eventBus.processAll();

        for (DispatchResult result : results) {
            printDispatchResult(result);
        }

        System.out.println("Pending events after processing: " + eventBus.getPendingEvents().size());
        System.out.println("Event history count: " + eventBus.getEventHistory().size());
        System.out.println("Dead-letter count: " + eventBus.getDeadLetterEvents().size());

        DeadLetterEvent firstDeadLetter = eventBus.getDeadLetterEvents().get(0);
        eventBus.replayDeadLetter(firstDeadLetter.getDeadLetterId());

        System.out.println("Pending events after replay: " + eventBus.getPendingEvents().size());

        DispatchResult replayResult = eventBus.processNext();
        printDispatchResult(replayResult);

        EventBusMetrics metrics = eventBus.getMetrics();

        System.out.println("Published events: " + metrics.getPublishedEvents());
        System.out.println("Processed events: " + metrics.getProcessedEvents());
        System.out.println("Successful observer calls: " + metrics.getSuccessfulObserverCalls());
        System.out.println("Failed observer calls: " + metrics.getFailedObserverCalls());
        System.out.println("Skipped observer calls: " + metrics.getSkippedObserverCalls());
        System.out.println("Duplicate skipped observer calls: " + metrics.getDuplicateSkippedObserverCalls());
        System.out.println("Dead-letter count: " + metrics.getDeadLetterCount());
    }

    private static void printDispatchResult(DispatchResult result) {
        if (result == null) {
            System.out.println("No event processed.");
            return;
        }

        System.out.println("Dispatch summary for event: " + result.getEvent().getEventType()
                + " order " + result.getEvent().getOrderId());
        System.out.println("Success: " + result.getSuccessCount());
        System.out.println("Failed: " + result.getFailedCount());
        System.out.println("Skipped: " + result.getSkippedCount());
        System.out.println("Duplicate skipped: " + result.getDuplicateSkippedCount());
    }
}
