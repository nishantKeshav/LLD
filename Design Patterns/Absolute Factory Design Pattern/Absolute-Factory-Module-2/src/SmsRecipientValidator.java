public class SmsRecipientValidator implements RecipientValidator {

    @Override
    public void validate(NotificationRequest request) {
        RecipientDetails recipientDetails = request.getRecipientDetails();
        if (recipientDetails == null) {
            throw new IllegalArgumentException("SMS recipient details cannot be null");
        }
        String phoneNumber = recipientDetails.getPhoneNumber();
        String countryCode = recipientDetails.getCountryCode();
        if (countryCode == null || countryCode.isBlank()) {
            throw new IllegalArgumentException("Country code cannot be null or blank.");
        }
        if (phoneNumber == null || phoneNumber.isBlank()) {
            throw new IllegalArgumentException("Phone number cannot be negative.");
        }
    }
}
