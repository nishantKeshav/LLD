package observer;

public class ObserverRegistration {

    private final int priority;
    private final int maxRetries;
    private final EventFilter eventFilter;
    private final OrderEventObserver observer;

    public ObserverRegistration(
            OrderEventObserver observer,
            int priority,
            int maxRetries,
            EventFilter eventFilter
    ) {
        this.observer = observer;
        this.priority = priority;
        this.maxRetries = maxRetries;
        this.eventFilter = eventFilter;
    }

    public OrderEventObserver getObserver() {
        return observer;
    }

    public int getPriority() {
        return priority;
    }

    public int getMaxRetries() {
        return maxRetries;
    }

    public EventFilter getEventFilter() {
        return eventFilter;
    }

}
