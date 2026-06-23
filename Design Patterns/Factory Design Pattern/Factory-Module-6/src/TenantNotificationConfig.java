import java.util.Set;

public class TenantNotificationConfig {

    private final String tenantId;
    private final Set<NotificationChannelType> enabledChannels;

    public TenantNotificationConfig(String tenantId, Set<NotificationChannelType> enabledChannels) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant ID cannot be null or blank.");
        }
        if (enabledChannels == null || enabledChannels.isEmpty()) {
            throw new IllegalArgumentException("Enabled channels cannot be null or empty.");
        }
        this.tenantId = tenantId;
        this.enabledChannels = Set.copyOf(enabledChannels);
    }

    public String getTenantId() {
        return tenantId;
    }

    public Set<NotificationChannelType> getEnabledChannels() {
        return enabledChannels;
    }

    public boolean isChannelEnabled(NotificationChannelType channelType) {
        return enabledChannels.contains(channelType);
    }
}