public class Main {

    public static void main(String[] args) {
        System.out.println("Factory Module 5 — Logistics Partner Factory");

        DeliveryAssignmentService service = new DeliveryAssignmentService();

        DeliveryRequest standardRequest = new DeliveryRequest(
                "REQ-101",
                "ORD-101",
                "CUST-101",
                "Mumbai",
                "Pune",
                "India",
                DeliveryType.STANDARD,
                150,
                8,
                5000,
                false,
                true
        );

        printResult(service.assignDeliveryPartner(standardRequest));

        DeliveryRequest expressRequest = new DeliveryRequest(
                "REQ-102",
                "ORD-102",
                "CUST-102",
                "Mumbai",
                "Bangalore",
                "India",
                DeliveryType.EXPRESS,
                980,
                12,
                15000,
                true,
                false
        );

        printResult(service.assignDeliveryPartner(expressRequest));

        DeliveryRequest sameDayRequest = new DeliveryRequest(
                "REQ-103",
                "ORD-103",
                "CUST-103",
                "Mumbai",
                "Thane",
                "India",
                DeliveryType.SAME_DAY,
                25,
                3,
                2000,
                false,
                true
        );

        printResult(service.assignDeliveryPartner(sameDayRequest));

        DeliveryRequest internationalRequest = new DeliveryRequest(
                "REQ-104",
                "ORD-104",
                "CUST-104",
                "Mumbai",
                "New York",
                "USA",
                DeliveryType.INTERNATIONAL,
                12500,
                20,
                50000,
                true,
                false
        );

        printResult(service.assignDeliveryPartner(internationalRequest));

        try {
            DeliveryRequest invalidSameDayRequest = new DeliveryRequest(
                    "REQ-105",
                    "ORD-105",
                    "CUST-105",
                    "Mumbai",
                    "Nashik",
                    "India",
                    DeliveryType.SAME_DAY,
                    180,
                    5,
                    3000,
                    false,
                    true
            );

            printResult(service.assignDeliveryPartner(invalidSameDayRequest));

        } catch (IllegalArgumentException exception) {
            System.out.println();
            System.out.println("Invalid delivery request rejected");
            System.out.println("Reason: " + exception.getMessage());
        }
    }

    private static void printResult(DeliveryAssignmentResult result) {
        System.out.println();
        System.out.println("==================================================");
        System.out.println("Request ID: " + result.getRequestId());
        System.out.println("Order ID: " + result.getOrderId());
        System.out.println("Customer ID: " + result.getCustomerId());
        System.out.println("Delivery Type: " + result.getDeliveryType());
        System.out.println("Partner Name: " + result.getPartnerName());
        System.out.println("Assigned: " + result.isAssigned());
        System.out.println("Message: " + result.getMessage());
        System.out.println("Estimated Delivery Days: " + result.getEstimatedDeliveryDays());
        System.out.println("Estimated Cost: " + result.getEstimatedCost());
        System.out.println("Tracking ID: " + result.getTrackingId());
        System.out.println("==================================================");
    }
}