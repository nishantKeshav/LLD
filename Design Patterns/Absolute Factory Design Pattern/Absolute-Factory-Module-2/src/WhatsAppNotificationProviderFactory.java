public class WhatsAppNotificationProviderFactory implements NotificationProviderFactory {

    @Override
    public RecipientValidator createRecipientValidator() {
        return new WhatsappRecipientValidator();
    }

    @Override
    public TemplateRenderer createTemplateRenderer() {
        return new WhatsAppTemplateRenderer();
    }

    @Override
    public NotificationSender createNotificationSender() {
        return new WhatsAppNotificationSender();
    }

    @Override
    public DeliveryTracker createDeliveryTracker() {
        return new WhatsAppDeliveryTracker();
    }

    @Override
    public RetryPolicy createRetryPolicy() {
        return new WhatsAppRetryPolicy();
    }
}
