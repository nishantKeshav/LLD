public class NotificationMetrics {

    private int sentCount;

    public void incrementSentCount() {
        sentCount++;
    }

    public int getSentCount() {
        return sentCount;
    }
}