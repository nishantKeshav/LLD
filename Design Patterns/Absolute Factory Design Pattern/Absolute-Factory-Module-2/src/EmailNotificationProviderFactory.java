public class EmailNotificationProviderFactory implements NotificationProviderFactory {

    @Override
    public RecipientValidator createRecipientValidator() {
        return new EmailRecipientValidator();
    }

    @Override
    public TemplateRenderer createTemplateRenderer() {
        return new EmailTemplateRenderer();
    }

    @Override
    public NotificationSender createNotificationSender() {
        return new EmailNotificationSender();
    }

    @Override
    public DeliveryTracker createDeliveryTracker() {
        return new EmailDeliveryTracker();
    }

    @Override
    public RetryPolicy createRetryPolicy() {
        return new EmailRetryPolicy();
    }
}