import java.util.Map;
import java.util.Set;

public class TenantNotificationConfigService {

    private final Map<String, TenantNotificationConfig> configs =
            Map.of(
                    "TENANT-A",
                    new TenantNotificationConfig(
                            "TENANT-A", Set.of(NotificationChannelType.EMAIL, NotificationChannelType.SMS, NotificationChannelType.PUSH)),
                    "TENANT-B",
                    new TenantNotificationConfig(
                            "TENANT-B", Set.of(NotificationChannelType.EMAIL, NotificationChannelType.WHATSAPP)),
                    "TENANT-C",
                    new TenantNotificationConfig("TENANT-C", Set.of(NotificationChannelType.PUSH))
            );

    public TenantNotificationConfig getConfig(String tenantId) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("Tenant Id cannot be null or blank");
        }
        TenantNotificationConfig config = configs.get(tenantId);
        if (config == null) {
            throw new IllegalArgumentException("Tenant Id does not exist");
        }
        return config;
    }
}
