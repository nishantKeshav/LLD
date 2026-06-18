package bus;

import event.DeadLetterEvent;
import event.OrderEvent;
import event.OrderEventType;
import metrics.EventBusMetrics;
import observer.EventFilter;
import observer.ObserverRegistration;
import observer.OrderEventObserver;
import result.DispatchResult;
import result.DispatchStatus;
import result.ObserverDispatchResult;

import java.util.*;

public class InMemoryQueuedEventBus implements QueuedEventBus {

    private final EventBusMetrics metrics;
    private final Queue<OrderEvent> eventQueue;
    private final List<OrderEvent> eventHistory;
    private final Set<String> processedObserverEventKeys;
    private final List<DeadLetterEvent> deadLetterEvents;
    private final Map<OrderEventType, List<ObserverRegistration>> observersByEventType;

    public InMemoryQueuedEventBus() {
        this.metrics = new EventBusMetrics();
        this.eventQueue = new ArrayDeque<>();
        this.eventHistory = new ArrayList<>();
        this.processedObserverEventKeys = new HashSet<>();
        this.deadLetterEvents = new ArrayList<>();
        this.observersByEventType = new EnumMap<>(OrderEventType.class);
    }

    @Override
    public void subscribe(OrderEventType eventType, ObserverRegistration registration) {
        if (eventType == null || registration == null || registration.getObserver() == null) {
            throw new IllegalArgumentException("Event type and observer registration cannot be null");
        }
        List<ObserverRegistration> registrations =
                observersByEventType.computeIfAbsent(eventType, key -> new ArrayList<>());
        boolean alreadySubscribed = registrations.stream()
                .anyMatch(existing ->
                        existing.getObserver()
                                .getObserverName()
                                .equals(registration.getObserver().getObserverName())
                );
        if (alreadySubscribed) {
            System.out.println(registration.getObserver().getObserverName()
                    + " is already subscribed to " + eventType.name());
            return;
        }
        registrations.add(registration);
        System.out.println(registration.getObserver().getObserverName()
                + " subscribed to " + eventType.name()
                + " with priority " + registration.getPriority());
    }

    @Override
    public void unsubscribe(OrderEventType eventType, OrderEventObserver observer) {
        if (eventType == null || observer == null) {
            throw new IllegalArgumentException("Event type and observer cannot be null");
        }
        List<ObserverRegistration> registrations = observersByEventType.get(eventType);
        if (registrations == null || registrations.isEmpty()) {
            System.out.println(observer.getObserverName()
                    + " is not subscribed to " + eventType.name());
            return;
        }
        boolean removed = registrations.removeIf(registration ->
                registration.getObserver().getObserverName().equals(observer.getObserverName())
        );
        if (!removed) {
            System.out.println(observer.getObserverName()
                    + " is not subscribed to " + eventType.name());
            return;
        }
        System.out.println(observer.getObserverName()
                + " unsubscribed from " + eventType.name());
    }

    @Override
    public void publish(OrderEvent event) {
        if (event == null) {
            throw new IllegalArgumentException("Cannot publish null event");
        }
        eventQueue.offer(event);
        eventHistory.add(event);
        metrics.incrementPublishedEvents();
        System.out.println("Event published to queue: "
                + event.getEventType().name()
                + " order "
                + event.getOrderId());
    }

    @Override
    public DispatchResult processNext() {
        OrderEvent event = eventQueue.poll();
        if (event == null) {
            System.out.println("No pending events to process.");
            return null;
        }
        metrics.incrementProcessedEvents();
        OrderEventType eventType = event.getEventType();
        List<ObserverRegistration> registrations = observersByEventType.get(eventType);
        List<ObserverDispatchResult> dispatchResultList = new ArrayList<>();
        if (registrations == null || registrations.isEmpty()) {
            System.out.println("No observers registered for event type " + eventType);
            return new DispatchResult(event, dispatchResultList);
        }
        List<ObserverRegistration> sortedRegistrations = registrations.stream()
                .sorted(Comparator.comparingInt(ObserverRegistration::getPriority))
                .toList();
        for (ObserverRegistration registration : sortedRegistrations) {
            OrderEventObserver observer = registration.getObserver();
            EventFilter eventFilter = registration.getEventFilter();
            String key = event.getEventId() + "::" + observer.getObserverName();
            // 1. Idempotency check
            if (processedObserverEventKeys.contains(key)) {
                System.out.println("Skipping already processed event "
                        + event.getEventId()
                        + " for observer "
                        + observer.getObserverName());
                dispatchResultList.add(new ObserverDispatchResult(
                        observer.getObserverName(),
                        event.getEventId(),
                        eventType,
                        DispatchStatus.DUPLICATE_SKIPPED,
                        0,
                        null
                ));
                metrics.incrementDuplicateSkippedObserverCalls();
                continue;
            }
            // 2. Filter check
            if (eventFilter != null && !eventFilter.shouldProcess(event)) {
                System.out.println("Observer "
                        + observer.getObserverName()
                        + " skipped for event "
                        + eventType
                        + " due to filter condition");

                dispatchResultList.add(new ObserverDispatchResult(
                        observer.getObserverName(),
                        event.getEventId(),
                        eventType,
                        DispatchStatus.SKIPPED,
                        0,
                        null
                ));

                metrics.incrementSkippedObserverCalls();
                continue;
            }
            // 3. Retry logic
            int attempts = 0;
            boolean success = false;
            String errorMessage = null;
            int maxAttempts = registration.getMaxRetries() + 1;
            while (!success && attempts < maxAttempts) {
                attempts++;
                try {
                    observer.onEvent(event);
                    success = true;
                    processedObserverEventKeys.add(key);
                } catch (Exception e) {
                    errorMessage = e.getMessage();
                    System.out.println("Observer "
                            + observer.getObserverName()
                            + " failed on attempt "
                            + attempts
                            + " for event "
                            + eventType
                            + ": "
                            + errorMessage);
                }
            }
            // 4. Result handling
            if (success) {
                dispatchResultList.add(new ObserverDispatchResult(
                        observer.getObserverName(),
                        event.getEventId(),
                        eventType,
                        DispatchStatus.SUCCESS,
                        attempts,
                        null
                ));
                metrics.incrementSuccessfulObserverCalls();
            } else {
                dispatchResultList.add(new ObserverDispatchResult(
                        observer.getObserverName(),
                        event.getEventId(),
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
                metrics.incrementFailedObserverCalls();
                metrics.incrementDeadLetterCount();
            }
        }
        return new DispatchResult(event, dispatchResultList);
    }

    @Override
    public List<DispatchResult> processAll() {
        List<DispatchResult> results = new ArrayList<>();
        while (!eventQueue.isEmpty()) {
            DispatchResult result = processNext();
            if (result != null) {
                results.add(result);
            }
        }
        return results;
    }

    @Override
    public void replayDeadLetter(String deadLetterId) {
        if (deadLetterId == null || deadLetterId.isBlank()) {
            throw new IllegalArgumentException("Dead-letter ID cannot be null or blank");
        }

        DeadLetterEvent matchedDeadLetterEvent = null;

        for (DeadLetterEvent deadLetterEvent : deadLetterEvents) {
            if (deadLetterEvent.getDeadLetterId().equals(deadLetterId)) {
                matchedDeadLetterEvent = deadLetterEvent;
                break;
            }
        }

        if (matchedDeadLetterEvent == null) {
            System.out.println("Dead-letter event not found with id: " + deadLetterId);
            return;
        }

        if (matchedDeadLetterEvent.isReplayed()) {
            System.out.println("Dead-letter event with id: "
                    + deadLetterId
                    + " has already been replayed.");
            return;
        }

        OrderEvent originalEvent = matchedDeadLetterEvent.getEvent();

        eventQueue.offer(originalEvent);
        matchedDeadLetterEvent.markReplayed();

        System.out.println("Dead-letter event replayed and added back to queue: " + deadLetterId);
    }

    @Override
    public List<OrderEvent> getPendingEvents() {
        return new ArrayList<>(eventQueue);
    }

    @Override
    public List<OrderEvent> getEventHistory() {
        return new ArrayList<>(eventHistory);
    }

    @Override
    public List<DeadLetterEvent> getDeadLetterEvents() {
        return new ArrayList<>(deadLetterEvents);
    }

    @Override
    public EventBusMetrics getMetrics() {
        return metrics;
    }
}
