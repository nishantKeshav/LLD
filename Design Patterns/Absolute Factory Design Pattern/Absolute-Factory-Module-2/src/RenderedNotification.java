public class RenderedNotification {

    private final String notificationId;
    private final NotificationProvider provider;
    private final RecipientDetails recipientDetails;
    private final String subject;
    private final String body;

    public RenderedNotification(String notificationId, NotificationProvider provider,
                                RecipientDetails recipientDetails, String subject, String body) {
        this.notificationId = notificationId;
        this.provider = provider;
        this.recipientDetails = recipientDetails;
        this.subject = subject;
        this.body = body;
    }

    public String getNotificationId() {
        return notificationId;
    }

    public NotificationProvider getProvider() {
        return provider;
    }

    public RecipientDetails getRecipientDetails() {
        return recipientDetails;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }
}
