public interface NotificationProviderFactory {
    RecipientValidator createRecipientValidator();
    TemplateRenderer createTemplateRenderer();
    NotificationSender createNotificationSender();
    DeliveryTracker createDeliveryTracker();
    RetryPolicy createRetryPolicy();
}