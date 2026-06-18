import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class NotificationService {

    private final Map<NotificationType, NotificationStrategy> strategyMap;

    public NotificationService(List<NotificationStrategy> strategies) {
        // create map from list of strategies
        strategyMap = new EnumMap<>(NotificationType.class);
        for (NotificationStrategy strategy : strategies) {
            strategyMap.put(strategy.getNotificationType(), strategy);
        }
    }

    public void sendNotification(NotificationType notificationType, String message) {
        NotificationStrategy strategy = strategyMap.get(notificationType);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy found for type: " + notificationType);
        }
        strategy.sendNotification(message);
    }
}