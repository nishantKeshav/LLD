public class RecipientDetails {

    private final String email;
    private final String phoneNumber;
    private final String customerId;
    private final String countryCode;

    public RecipientDetails(
            String customerId,
            String email,
            String phoneNumber,
            String countryCode
    ) {
        if (customerId == null || customerId.isBlank()) {
            throw new IllegalArgumentException("Customer ID cannot be null or blank");
        }

        if ((email == null || email.isBlank())
                && (phoneNumber == null || phoneNumber.isBlank())) {
            throw new IllegalArgumentException("Either email or phone number must be present");
        }

        this.customerId = customerId;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.countryCode = countryCode;
    }

    public String getEmail() {
        return email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getCountryCode() {
        return countryCode;
    }
}
