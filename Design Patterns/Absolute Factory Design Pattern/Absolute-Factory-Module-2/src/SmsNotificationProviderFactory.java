public class SmsNotificationProviderFactory implements NotificationProviderFactory {

    @Override
    public RecipientValidator createRecipientValidator() {
        return new SmsRecipientValidator();
    }

    @Override
    public TemplateRenderer createTemplateRenderer() {
        return new SmsTemplateRenderer();
    }

    @Override
    public NotificationSender createNotificationSender() {
        return new SmsNotificationSender();
    }

    @Override
    public DeliveryTracker createDeliveryTracker() {
        return new SmsDeliveryTracker();
    }

    @Override
    public RetryPolicy createRetryPolicy() {
        return new SmsRetryPolicy();
    }

}
