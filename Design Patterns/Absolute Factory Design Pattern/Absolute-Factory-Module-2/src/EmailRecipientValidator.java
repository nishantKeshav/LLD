public class EmailRecipientValidator implements RecipientValidator {

    @Override
    public void validate(NotificationRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Notification request cannot be null");
        }

        RecipientDetails recipientDetails = request.getRecipientDetails();

        if (recipientDetails == null) {
            throw new IllegalArgumentException("Email recipient details cannot be null");
        }

        String email = recipientDetails.getEmail();

        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email cannot be null or blank.");
        }

        if (!email.contains("@") || !email.contains(".")) {
            throw new IllegalArgumentException("Invalid email address for EMAIL notification");
        }
    }
}