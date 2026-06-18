package observable;

import event.OrderEvent;
import event.OrderEventType;
import observer.OrderEventObserver;

import java.util.*;

public class PriorityRetryEventDispatcher implements AdvancedEventDispatcher{

    private final List<OrderEvent> eventHistory;
    private final List<DeadLetterEvent> deadLetterEvents;
    private final Map<OrderEventType, List<ObserverRegistration>> observersByEventType;

    public PriorityRetryEventDispatcher() {
        this.eventHistory = new ArrayList<>();
        this.deadLetterEvents = new ArrayList<>();
        this.observersByEventType = new EnumMap<>(OrderEventType.class);
    }

    @Override
    public void subscribe(OrderEventType eventType, ObserverRegistration registration) {
        List<ObserverRegistration> observerRegistrations =
                observersByEventType.computeIfAbsent(eventType, key -> new ArrayList<>());
        boolean alreadySubscribed = observerRegistrations.stream()
                .anyMatch(existing ->
                        existing.getObserver().getObserverName()
                                .equals(registration.getObserver().getObserverName())
                );
        if (alreadySubscribed) {
            System.out.println(registration.getObserver().getObserverName() + " is already subscribed to " + eventType.name());
            return;
        }
        observerRegistrations.add(registration);
        System.out.println(registration.getObserver().getObserverName() + " subscribed to " + eventType.name() + " with priority " + registration.getPriority());
    }

    @Override
    public void unsubscribe(OrderEventType eventType, OrderEventObserver observer) {
        List<ObserverRegistration> observerRegistrations = observersByEventType.get(eventType);
        if (observerRegistrations == null || observerRegistrations.isEmpty()) {
            System.out.println(observer.getObserverName() + " is not subscribed to " + eventType.name());
            return;
        }
        boolean removed = observerRegistrations.removeIf(registration ->
                registration.getObserver().getObserverName().equals(observer.getObserverName())
        );
        if (!removed) {
            System.out.println(observer.getObserverName() + " is not subscribed to " + eventType.name());
            return;
        }
        System.out.println(observer.getObserverName() + " unsubscribed from " + eventType.name());
    }

    @Override
    public DispatchResult publish(OrderEvent event) {
        eventHistory.add(event);
        List<ObserverDispatchResult> observerResults = new ArrayList<>();
        OrderEventType eventType = event.getEventType();
        List<ObserverRegistration> observerRegistrations = observersByEventType.get(eventType);
        if (observerRegistrations == null || observerRegistrations.isEmpty()) {
            return new DispatchResult(event, observerResults);
        }
        List<ObserverRegistration> sortedRegistrations = observerRegistrations.stream()
                .sorted(Comparator.comparingInt(ObserverRegistration::getPriority))
                .toList();
        for (ObserverRegistration observerRegistration : sortedRegistrations) {
            OrderEventObserver observer = observerRegistration.getObserver();
            EventFilter eventFilter = observerRegistration.getEventFilter();
            if (eventFilter != null && !eventFilter.shouldProcess(event)) {
                observerResults.add(new ObserverDispatchResult(observer.getObserverName(),
                        eventType, DispatchStatus.SKIPPED, 0, null));
                continue;
            }
            int attempts = 0;
            boolean success = false;
            String errorMessage = null;
            int maxAttempts = observerRegistration.getMaxRetries() + 1;
            while (attempts < maxAttempts && !success) {
                attempts++;
                try {
                    observer.onEvent(event);
                    success = true;
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    System.out.println("Observer " + observer.getObserverName() + " failed on attempt " + attempts + " for event " + eventType + ": " + errorMessage);
                }
            }
            if (success) {
                observerResults.add(new ObserverDispatchResult(
                        observer.getObserverName(),
                        eventType,
                        DispatchStatus.SUCCESS,
                        attempts,
                        null
                ));
            } else {
                observerResults.add(new ObserverDispatchResult(
                        observer.getObserverName(),
                        eventType,
                        DispatchStatus.FAILED,
                        attempts,
                        errorMessage
                ));
                deadLetterEvents.add(new DeadLetterEvent(
                        event,
                        observer.getObserverName(),
                        errorMessage,
                        attempts
                ));
            }
        }
        return new DispatchResult(event, observerResults);
    }

    @Override
    public List<OrderEvent> getEventHistory() {
        return new ArrayList<>(eventHistory);
    }

    @Override
    public List<DeadLetterEvent> getDeadLetterEvents() {
        return new ArrayList<>(deadLetterEvents);
    }
}
