public class NotificationDispatchService {

    private final NotificationProviderFactorySelector notificationProviderFactorySelector;

    public NotificationDispatchService(
            NotificationProviderFactorySelector notificationProviderFactorySelector
    ) {
        if (notificationProviderFactorySelector == null) {
            throw new IllegalArgumentException("notificationProviderFactorySelector cannot be null");
        }

        this.notificationProviderFactorySelector = notificationProviderFactorySelector;
    }

    public NotificationDispatchResult dispatch(NotificationRequest request) {

        validateRequest(request);

        NotificationProvider provider = request.getProvider();

        try {
            NotificationProviderFactory factory = notificationProviderFactorySelector.getFactory(provider);

            RecipientValidator recipientValidator = factory.createRecipientValidator();
            TemplateRenderer templateRenderer = factory.createTemplateRenderer();
            NotificationSender notificationSender = factory.createNotificationSender();
            RetryPolicy retryPolicy = factory.createRetryPolicy();
            DeliveryTracker deliveryTracker = factory.createDeliveryTracker();

            recipientValidator.validate(request);

            RenderedNotification renderedNotification = templateRenderer.render(request);

            SendResult sendResult = notificationSender.send(renderedNotification);

            RetryDecision retryDecision = retryPolicy.evaluate(request, sendResult);

            if (!sendResult.isSuccess()) {
                DeliveryStatus finalStatus = retryDecision.getShouldRetry()
                        ? DeliveryStatus.RETRY_SCHEDULED
                        : DeliveryStatus.FAILED;

                return new NotificationDispatchResult(
                        request.getNotificationId(),
                        provider,
                        false,
                        finalStatus,
                        null,
                        "Notification dispatch failed",
                        retryDecision,
                        sendResult.getFailureMessage()
                );
            }

            DeliveryTrackingRequest trackingRequest =
                    new DeliveryTrackingRequest(
                            request.getNotificationId(),
                            sendResult.getProviderMessageId(),
                            provider
                    );

            DeliveryTrackingResult trackingResult = deliveryTracker.track(trackingRequest);

            return new NotificationDispatchResult(
                    request.getNotificationId(),
                    provider,
                    true,
                    trackingResult.getDeliveryStatus(),
                    sendResult.getProviderMessageId(),
                    provider + " notification dispatched successfully",
                    retryDecision,
                    null
            );

        } catch (IllegalArgumentException exception) {
            RetryDecision retryDecision = new RetryDecision(
                    false,
                    0,
                    0,
                    "No retry because dispatch failed before send stage"
            );

            return new NotificationDispatchResult(
                    request.getNotificationId(),
                    provider,
                    false,
                    DeliveryStatus.FAILED,
                    null,
                    "Notification dispatch failed",
                    retryDecision,
                    exception.getMessage()
            );
        }
    }

    private void validateRequest(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null");
        }

        if (request.getNotificationId() == null || request.getNotificationId().isBlank()) {
            throw new IllegalArgumentException("Notification ID cannot be null or blank");
        }

        if (request.getProvider() == null) {
            throw new IllegalArgumentException("Notification provider cannot be null");
        }

        if (request.getType() == null) {
            throw new IllegalArgumentException("Notification type cannot be null");
        }

        if (request.getPriority() == null) {
            throw new IllegalArgumentException("Notification priority cannot be null");
        }

        if (request.getRecipientDetails() == null) {
            throw new IllegalArgumentException("Recipient details cannot be null");
        }

        if (request.getTemplateCode() == null || request.getTemplateCode().isBlank()) {
            throw new IllegalArgumentException("Template code cannot be null or blank");
        }

        if (request.getTemplateVariables() == null) {
            throw new IllegalArgumentException("Template variables cannot be null");
        }
    }
}