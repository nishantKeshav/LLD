package observable;

import event.OrderEvent;
import event.OrderEventType;
import observer.OrderEventObserver;

import java.util.Map;
import java.util.List;
import java.util.EnumMap;
import java.util.ArrayList;

public class SimpleEventDispatcher implements EventDispatcher {

    private final List<OrderEvent> eventHistoryList;
    private final Map<OrderEventType, List<OrderEventObserver>> orderEventObserverMap;

    public SimpleEventDispatcher() {
        this.eventHistoryList = new ArrayList<>();
        this.orderEventObserverMap = new EnumMap<>(OrderEventType.class);
    }

    @Override
    public void subscribe(OrderEventType eventType, OrderEventObserver observer) {
        List<OrderEventObserver> observerList = orderEventObserverMap
                .computeIfAbsent(eventType, key -> new ArrayList<>());

        if (observerList.contains(observer)) {
            System.out.println("Observer " + observer.getObserverName()
                    + " is already subscribed to event type " + eventType);
            return;
        }

        observerList.add(observer);

        System.out.println("Observer " + observer.getObserverName()
                + " subscribed to event type " + eventType);
    }

    @Override
    public void unsubscribe(OrderEventType eventType, OrderEventObserver observer) {
        List<OrderEventObserver> observerList = orderEventObserverMap.get(eventType);

        if (observerList == null || observerList.isEmpty()) {
            System.out.println("No observers found for event type " + eventType);
            return;
        }

        boolean removed = observerList.remove(observer);

        if (!removed) {
            System.out.println("Observer " + observer.getObserverName()
                    + " is not subscribed to event type " + eventType);
            return;
        }

        System.out.println("Observer " + observer.getObserverName()
                + " unsubscribed from event type " + eventType);
    }

    @Override
    public void publish(OrderEvent event) {
        eventHistoryList.add(event);

        OrderEventType eventType = event.getEventType();

        List<OrderEventObserver> observerList = orderEventObserverMap.get(eventType);

        if (observerList == null || observerList.isEmpty()) {
            System.out.println("No observers found for event type " + eventType);
            return;
        }

        List<OrderEventObserver> observersToNotify = new ArrayList<>(observerList);

        for (OrderEventObserver observer : observersToNotify) {
            updateOnEvent(observer, event);
        }
    }

    private void updateOnEvent(OrderEventObserver observer, OrderEvent event) {
        try {
            observer.onEvent(event);
        } catch (Exception e) {
            System.out.println("Error while updating observer "
                    + observer.getObserverName()
                    + ": "
                    + e.getMessage());
        }
    }

    @Override
    public List<OrderEvent> getEventHistory() {
        return new ArrayList<>(eventHistoryList);
    }
}