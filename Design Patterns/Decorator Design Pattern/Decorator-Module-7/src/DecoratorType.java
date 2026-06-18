public enum DecoratorType {

    TIMEOUT(10),
    LOGGING(20),
    AUTH(30),
    REQUEST_ID(40),
    RETRY(50),
    METRICS(60);

    private final int priority;

    DecoratorType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}