import java.util.List;
import java.util.ArrayList;

public class NotificationRoutingService {

    private final NotificationChannelFactory channelFactory;
    private final TenantNotificationConfigService configService;

    public NotificationRoutingService(NotificationChannelFactory channelFactory, TenantNotificationConfigService configService) {
        if (channelFactory == null) {
            throw new IllegalArgumentException("channelFactory cannot be null");
        }
        if (configService == null) {
            throw new IllegalArgumentException("configService cannot be null");
        }
        this.configService = configService;
        this.channelFactory = channelFactory;
    }

    public NotificationChannel resolveChannel(NotificationRequest request, List<NotificationChannelType> attemptedChannels) {
        String tenantId = request.getTenantId();
        TenantNotificationConfig notificationConfig = configService.getConfig(tenantId);

        List<NotificationChannelType> candidates = new ArrayList<>();
        candidates.add(request.getPreferredChannel());
        candidates.addAll(request.getFallbackChannels());

        for (NotificationChannelType channelType : candidates) {
            attemptedChannels.add(channelType);
            if (!notificationConfig.isChannelEnabled(channelType)) {
                continue;
            }
            NotificationChannel resolvedChannel = channelFactory.getChannel(channelType);
            if (!resolvedChannel.supportsPriority(request.getPriority())) {
                continue;
            }
            if (!hasRequiredRecipientDetails(request, channelType)) {
                continue;
            }
            return resolvedChannel;
        }
        throw new IllegalArgumentException("No suitable notification channel found for request: " + request.getNotificationId());
    }

    private boolean hasRequiredRecipientDetails(NotificationRequest request, NotificationChannelType channelType) {
        return switch (channelType) {
            case PUSH -> request.getDeviceToken() != null && !request.getDeviceToken().isBlank();
            case EMAIL -> request.getRecipientEmail() != null && !request.getRecipientEmail().isBlank();
            case SMS, WHATSAPP -> request.getRecipientPhone() != null && !request.getRecipientPhone().isBlank();
            default -> throw new IllegalArgumentException("Unknown notification channel type: " + channelType);
        };
    }

}
