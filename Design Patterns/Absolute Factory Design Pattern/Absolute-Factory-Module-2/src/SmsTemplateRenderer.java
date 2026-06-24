import java.util.Map;
import java.util.stream.Collectors;

public class SmsTemplateRenderer implements TemplateRenderer {

    @Override
    public RenderedNotification render(NotificationRequest request) {
        String body = "Hello "
                + request.getTemplateVariables().getOrDefault("userName", "Customer")
                + ", your " + NotificationProvider.SMS.toString().toLowerCase() + " notification for template "
                + request.getTemplateCode()
                + " has been generated. Details: "
                + renderTemplateVariables(request.getTemplateVariables());
        return new RenderedNotification(
                request.getNotificationId(),
                NotificationProvider.SMS,
                request.getRecipientDetails(),
                NotificationProvider.SMS + " Notification: " + request.getTemplateCode(),
                body
        );
    }

    private String renderTemplateVariables(Map<String, String> templateVariables) {
        if (templateVariables == null || templateVariables.isEmpty()) {
            return "No additional details";
        }
        return templateVariables.entrySet()
                .stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", "));
    }
}
